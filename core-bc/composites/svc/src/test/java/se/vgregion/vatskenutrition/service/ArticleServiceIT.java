package se.vgregion.vatskenutrition.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.vgregion.vatskenutrition.config.AppTestConfig;
import se.vgregion.vatskenutrition.model.Article;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppTestConfig.class)
public class ArticleServiceIT {

    @Autowired
    private ArticleService articleService;

    @Test
    public void findAllArticles() throws Exception {
        List<Article> allArticles = articleService.findAllArticles();
    }

    @Test
    public void findByYear() throws Exception {
        List<Article> byYear = articleService.findByYear("2017", true);
    }

    @Test
    public void fetchArticlesFromExternalSource() throws Exception {
        List<Article> articles = articleService.fetchArticlesFromExternalSource();
    }

}