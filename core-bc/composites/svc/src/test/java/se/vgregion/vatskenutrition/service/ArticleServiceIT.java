package se.vgregion.vatskenutrition.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.vgregion.vatskenutrition.config.AppITConfig;
import se.vgregion.vatskenutrition.model.Article;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppITConfig.class)
public class ArticleServiceIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleServiceIT.class);

    @Value("${fetchAllArticlesUrl}")
    private String fetchAllArticlesUrl;

    @Autowired
    private ArticleService articleService;

    @Test
    public void findAllArticles() throws Exception {
        List<Article> allArticles = articleService.findAllArticles();
    }

    @Test
    public void findByYear() throws Exception {
        List<Article> byYear = articleService.findByYear("2017");
    }

    @Test
    @Ignore
    public void fetchArticlesFromExternalSource() throws Exception {
        List<Article> articles = articleService.fetchArticlesFromExternalSource(fetchAllArticlesUrl);

        LOGGER.info("Articles size: " + articles.size());

        List<Article> byYear = articleService.findByYear("2017");

        LOGGER.info("Articles by year size: " + byYear.size());

    }

}