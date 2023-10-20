package se.vgregion.vatskenutrition.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import se.vgregion.vatskenutrition.model.v2.Article;
import se.vgregion.vatskenutrition.model.v2.Folder;
import se.vgregion.vatskenutrition.model.v2.ItemResponse;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
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

    @Value("${api.username}")
    private String username;
    @Value("${api.password}")
    private String password;

    @Value("${fetchAllArticlesUrl}")
    private String fetchAllArticlesUrl;

    @Value("${fetchStartPageArticlesUrl}")
    private String fetchStartPageArticlesUrl;

    @Value("${fetchFolderUrl}")
    private String fetchFolderUrl;

    @Value("${defaultRevision}")
    private String defaultRevision;

    private List<Article> startPageArticles;
    private List<Article> allArticles;
    private Map<Integer, Folder> allFolders;
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
            CompletableFuture<Object> completableFuture3 = new CompletableFuture<>();

            fetchArticlesFromExternalSource(fetchAllArticlesUrl,
                    new ListenableFutureCallback<ResponseEntity<ItemResponse<Article>>>() {

                @Override
                public void onFailure(Throwable throwable) {
                    LOGGER.error(throwable.getMessage(), throwable);
                    completableFuture1.complete(null);
                }

                @Override
                public void onSuccess(ResponseEntity<ItemResponse<Article>> responseEntity) {
                    try {
                        List<Article> articles = responseEntity.getBody().getItems();

                        articles.forEach(article -> {
                            LinkedList<String> path = new LinkedList<>();
                            Integer folderId = article.getStructuredContentFolderId();

                            Folder folder = allFolders.get(folderId);
                            path.addFirst(folder.getName());
                            while (folder.getParentStructuredContentFolderId() != null) {
                                folder = allFolders.get(folder.getParentStructuredContentFolderId());
                                path.addFirst(folder.getName());
                            }

                            article.setPath(path);
                        });

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
            }, Article.class);

            fetchArticlesFromExternalSource(fetchStartPageArticlesUrl,
                    new ListenableFutureCallback<ResponseEntity<ItemResponse<Article>>>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            LOGGER.error(throwable.getMessage(), throwable);
                            completableFuture2.complete(new Object());
                        }

                        @Override
                        public void onSuccess(ResponseEntity<ItemResponse<Article>> responseEntity) {
                            try {
                                startPageArticles = Objects.requireNonNull(responseEntity.getBody()).getItems();
                                completableFuture2.complete(new Object());
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage(), e);
                            }
                        }
                    }, Article.class);

            fetchArticlesFromExternalSource(fetchFolderUrl,
                    new ListenableFutureCallback<ResponseEntity<ItemResponse<Folder>>>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            LOGGER.error(throwable.getMessage(), throwable);
                            completableFuture3.complete(new Object());
                        }

                        @Override
                        public void onSuccess(ResponseEntity<ItemResponse<Folder>> responseEntity) {
                            try {
                                List<Folder> folders = Objects.requireNonNull(responseEntity.getBody()).getItems();

                                Map<Integer, Folder> newMap = new HashMap<>();

                                for (Folder folder : folders) {
                                    newMap.put(folder.getId(), folder);
                                }

                                allFolders = newMap;
                                completableFuture3.complete(new Object());
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage(), e);
                            }
                        }
                    }, Folder.class);

            if (doneConsumer.isPresent()) {
                CompletableFuture.allOf(completableFuture1, completableFuture2, completableFuture3)
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
        return allArticles.stream().filter(a -> getRootFolder(a).getName().contains(year)).collect(Collectors.toList());
    }

    public Folder getRootFolder(Article article) {
        Integer folderId = article.getStructuredContentFolderId();

        Folder folder = allFolders.get(folderId);

        while (folder.getParentStructuredContentFolderId() != null) {
            folder = allFolders.get(folder.getParentStructuredContentFolderId());
        }

        return folder;
    }

    public List<String> findAvailableYears() {
        Set<String> years = new TreeSet<>();

        for (Article article : allArticles) {
            years.add(getRootFolder(article).getName());
        }

        return new ArrayList<>(years);
    }

    public String getDefaultRevision() {
        return defaultRevision;
    }

    public Article findStartPageArticle(String year) {
        return startPageArticles.stream()
                .filter(article -> getRootFolder(article).getName().equals(year))
                .findFirst()
                .orElse(null);
    }

    <T> void fetchArticlesFromExternalSource(String url,
                                             ListenableFutureCallback<ResponseEntity<ItemResponse<T>>> callback,
                                             Class<T> clazz) {

        try {

            AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();

            ListenableFuture<ResponseEntity<ItemResponse<T>>> listenableFuture = asyncRestTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(null, createHeaders(username, password)),
                    new ParameterizedTypeReference<>() {
                        @Override
                        public Type getType() {
                            return new ObjectMapper().getTypeFactory().constructParametricType(ItemResponse.class, clazz);
                        }
                    }
            );

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
            getRootFolder(article).getName().equals(year) && article.getTitle().equals(articleTitle)
        ).findFirst().orElse(null);
    }
}
