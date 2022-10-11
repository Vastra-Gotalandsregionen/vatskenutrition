package se.vgregion.vatskenutrition.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import se.vgregion.vatskenutrition.model.Article;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Patrik Björk
 */
@Service
public class ArticleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleService.class);

    @Value("${fetchAllArticlesUrl}")
    private String fetchAllArticlesUrl;

    @Value("${fetchStartPageArticlesUrl}")
    private String fetchStartPageArticlesUrl;

    @Value("${defaultRevision}")
    private String defaultRevision;

    private List<Article> startPageArticles;
    private List<Article> allArticles;
    private Map<String, Article> allArticlesByUuid;

    private static Lock dbLock = new ReentrantLock();

    @PostConstruct
    @Scheduled(fixedRate = 600_000)
    public synchronized void update() {
        update(Optional.empty());
    }

    void update(Optional<CompletableFuture<Object>> doneConsumer) {
        try {
            if (fetchAllArticlesUrl == null || "".equals(fetchAllArticlesUrl)) {
                LOGGER.warn("fetchAllArticlesUrl is not set. Skip fetch articles.");
                return;
            }

            CompletableFuture<Object> completableFuture1 = new CompletableFuture<>();
            CompletableFuture<Object> completableFuture2 = new CompletableFuture<>();

            fetchArticlesFromExternalSource(fetchAllArticlesUrl,
                    new ListenableFutureCallback<ResponseEntity<Article[]>>() {

                @Override
                public void onFailure(Throwable throwable) {
                    LOGGER.error(throwable.getMessage(), throwable);
                    completableFuture1.complete(null);
                }

                @Override
                public void onSuccess(ResponseEntity<Article[]> responseEntity) {
                    try {
                        List<Article> articles = Arrays.asList(responseEntity.getBody());

                        dbLock.lock();

                        allArticles = new ArrayList<>(articles);

                        allArticlesByUuid = allArticles.stream().collect(Collectors.toUnmodifiableMap(
                                Article::getUuid,
                                Function.identity()
                        ));

                        completableFuture1.complete(null);
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    } finally {
                        dbLock.unlock();
                    }
                }
            });

            fetchArticlesFromExternalSource(fetchStartPageArticlesUrl,
                    new ListenableFutureCallback<ResponseEntity<Article[]>>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            LOGGER.error(throwable.getMessage(), throwable);
                            completableFuture2.complete(new Object());
                        }

                        @Override
                        public void onSuccess(ResponseEntity<Article[]> responseEntity) {
                            try {
                                startPageArticles = Arrays.asList(responseEntity.getBody());
                                completableFuture2.complete(new Object());
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage(), e);
                            }
                        }
                    });

            if (doneConsumer.isPresent()) {
                CompletableFuture.allOf(completableFuture1, completableFuture2)
                        .thenAccept(o -> doneConsumer.get().complete(new Object()));
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public List<Article> findAllArticles() {
        return new ArrayList<>(allArticles);
    }

    public List<Article> findByYear(String year) {
        return allArticles.stream().filter(a -> a.getPaths().contains(year)
                && (a.getStatus() == 0)).collect(Collectors.toList());
    }

    public List<String> findAvailableYears() {
        Set<String> years = new TreeSet<>();

        for (Article article : allArticles) {
            if (article.getPaths() != null && article.getPaths().size() > 0) {
                years.add(article.getPaths().get(0));
            }
        }

        return new ArrayList<>(years);
    }

    public String getDefaultRevision() {
        return defaultRevision;
    }

    public Article findStartPageArticle(String year) {
        return startPageArticles.stream()
                .filter(article -> article.getPaths().get(0).equals(year))
                .findFirst()
                .orElse(null);
    }

    void fetchArticlesFromExternalSource(String url, ListenableFutureCallback<ResponseEntity<Article[]>> callback) {

        try {

            AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();

            ListenableFuture<ResponseEntity<Article[]>> listenableFuture = asyncRestTemplate.getForEntity(
                    url,
                    Article[].class,
                    new HashMap<>());

            listenableFuture.addCallback(callback);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    HttpHeaders createHeaders(String username, String password) {
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.getEncoder().encode(
                    auth.getBytes(Charset.forName("UTF-8")));
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
        }};
    }

    public Article findArticle(String articleUuid) {
        return allArticlesByUuid.get(articleUuid);
    }

    public Article findArticle(String articleTitle, String year) {
        return allArticles.stream().filter(article ->
            article.getPaths().get(0).equals(year) && article.getTitle().equals(articleTitle)
        ).findFirst().orElse(null);
    }
}
