package se.vgregion.vatskenutrition.service;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.concurrent.ListenableFutureCallback;
import se.vgregion.vatskenutrition.config.AppITConfig;
import se.vgregion.vatskenutrition.model.Article;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppITConfig.class)
public class ArticleServiceIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleServiceIT.class);

    private static ClientAndServer mockServer;

    @Value("${fetchAllArticlesUrl}")
    private String fetchAllArticlesUrl;

    @Value("${fetchStartPageArticlesUrl}")
    private String fetchStartPageArticlesUrl;

    @Autowired
    private ArticleService articleService;

    @BeforeClass
    public static void startProxy() throws IOException {
        String fetchAllArticlesResponse = IOUtils.toString(ArticleServiceIT.class.getClassLoader().getResourceAsStream(
                "fetchAllArticlesResponse.json"), "UTF-8");

        String fetchStartPageArticlesResponse = IOUtils.toString(ArticleServiceIT.class.getClassLoader().getResourceAsStream(
                "fetchStartPageArticlesResponse.json"), "UTF-8");

        mockServer = startClientAndServer(8877);
        
        mockServer.when(HttpRequest.request("/api/jsonws/skinny-web.skinny/get-skinny-journal-articles/company-id/" +
                "10136/group-name/vatskenutrition/ddm-structure-id/1687613/locale/sv_SE/").withMethod("GET"))
                .respond(HttpResponse.response(fetchAllArticlesResponse)
                        .withHeader("Content-Type", "application/json;charset=UTF-8"));
        
        mockServer.when(HttpRequest.request("/api/jsonws/skinny-web.skinny/get-skinny-journal-articles/company-id/" +
                "10136/group-name/vatskenutrition/ddm-structure-id/1695303/locale/sv_SE").withMethod("GET"))
                .respond(HttpResponse.response(fetchStartPageArticlesResponse)
                        .withHeader("Content-Type", "application/json;charset=UTF-8"));
    }

    @AfterClass
    public static void stopProxy() {
        mockServer.stop();
    }

    @Test
    public void findAllArticles() throws Exception {
        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        CountDownLatch lock = new CountDownLatch(1);

        // Given (preparation)
        articleService.update(Optional.of(completableFuture));

        // Just to wait...
        completableFuture.whenComplete((o, v) -> {
            // When
            List<Article> allArticles = articleService.findAllArticles();

            // Then
            assertTrue(allArticles.size() > 0);

            lock.countDown();
        });

        lock.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void findByYear() throws Exception {

        CountDownLatch lock = new CountDownLatch(1);

        CompletableFuture<Object> completableFuture = new CompletableFuture<>();

        // Given (preparation)
        articleService.update(Optional.of(completableFuture));

        // Just to wait...
        completableFuture.whenComplete((o, v) -> {
            // When
            List<Article> byYear = articleService.findByYear("2017");

            // Then
            assertTrue(byYear.size() > 0);

            lock.countDown();
        });

        lock.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void fetchArticlesFromExternalSource() throws Exception {
        CountDownLatch lock = new CountDownLatch(1);
        final List<Article>[] articles = new List[]{null};

        ListenableFutureCallback<ResponseEntity<Article[]>> callback = new ListenableFutureCallback<ResponseEntity<Article[]>>() {

            @Override
            public void onFailure(Throwable ex) {
                ex.printStackTrace();
            }

            @Override
            public void onSuccess(ResponseEntity<Article[]> result) {
                articles[0] = Arrays.asList(result.getBody());

                lock.countDown();
            }
        };
        articleService.fetchArticlesFromExternalSource(
                fetchAllArticlesUrl, callback);

        lock.await(5, TimeUnit.SECONDS);

        assertNotNull(articles[0]);

        LOGGER.info("Articles size: " + articles[0].size());
    }

    @Test
    public void findStartPageArticle() throws Exception {
        Article startPageArticle = articleService.findStartPageArticle("2017");

        assertNotNull(startPageArticle);
    }

}