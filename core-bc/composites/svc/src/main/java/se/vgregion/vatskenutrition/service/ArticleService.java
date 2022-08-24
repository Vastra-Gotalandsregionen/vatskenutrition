package se.vgregion.vatskenutrition.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import se.vgregion.vatskenutrition.model.Article;
import se.vgregion.vatskenutrition.repository.ArticleRepository;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Patrik Björk
 */
@Service
public class ArticleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleService.class);

    @Autowired
    private ArticleRepository articleRepository;

    @Value("${fetchAllArticlesUrl}")
    private String fetchAllArticlesUrl;

    @Value("${fetchStartPageArticlesUrl}")
    private String fetchStartPageArticlesUrl;

    @Value("${defaultRevision}")
    private String defaultRevision;

    private List<Article> startPageArticles;

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

                        articleRepository.deleteAll();
                        articleRepository.save(articles);

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
        return articleRepository.findAll();
    }

    public List<Article> findByYear(String year) {
        return articleRepository.findArticles(year, 0);
    }

    public List<String> findAvailableYears() {
        return articleRepository.findAvailableYears();
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
        Article article = articleRepository.findOne(articleUuid);

        if (article == null) {
            return null;
        }

        return article;
    }
}
