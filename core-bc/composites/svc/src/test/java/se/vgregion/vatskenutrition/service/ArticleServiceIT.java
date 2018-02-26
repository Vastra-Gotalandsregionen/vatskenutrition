package se.vgregion.vatskenutrition.service;

import org.apache.commons.io.IOUtils;
import org.junit.After;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.vgregion.vatskenutrition.config.AppITConfig;
import se.vgregion.vatskenutrition.model.Article;

import java.io.IOException;
import java.util.List;

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

    @After
    public void stopProxy() {
        mockServer.stop();
    }

    @Test
    public void findAllArticles() throws Exception {
        List<Article> allArticles = articleService.findAllArticles();
        System.out.println(allArticles);
        assertTrue(allArticles.size() > 0);
    }

    @Test
    public void findByYear() throws Exception {
        List<Article> byYear = articleService.findByYear("2017");
        assertTrue(byYear.size() > 0);
    }

    @Test
    public void fetchArticlesFromExternalSource() throws Exception {
        List<Article> articles = articleService.fetchArticlesFromExternalSource(fetchAllArticlesUrl);

        LOGGER.info("Articles size: " + articles.size());
    }

    @Test
    public void findStartPageArticle() throws Exception {
        Article startPageArticle = articleService.findStartPageArticle("2017");

        assertNotNull(startPageArticle);
    }

}