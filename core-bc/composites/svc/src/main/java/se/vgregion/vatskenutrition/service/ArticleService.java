package se.vgregion.vatskenutrition.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import se.vgregion.vatskenutrition.model.Article;
import se.vgregion.vatskenutrition.model.Child;
import se.vgregion.vatskenutrition.model.Field;
import se.vgregion.vatskenutrition.repository.ArticleRepository;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

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

    @PostConstruct
    @Scheduled(fixedRate = 600_000)
    public synchronized void update() {
        try {
            if (fetchAllArticlesUrl == null || "".equals(fetchAllArticlesUrl)) {
                LOGGER.warn("fetchAllArticlesUrl is not set. Skip fetch articles.");
                return;
            }

            List<Article> articles = fetchArticlesFromExternalSource(fetchAllArticlesUrl);
            startPageArticles = fetchArticlesFromExternalSource(fetchStartPageArticlesUrl);

            articleRepository.deleteAll();
            articleRepository.save(articles);
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

    public List<Article> fetchArticlesFromExternalSource(String url) {

        try {

            RestTemplate skinnyTemplate = new RestTemplate();

            ResponseEntity<Article[]> skinnyResponse = skinnyTemplate.getForEntity(
                    url,
                    Article[].class,
                    new HashMap<>());

            Article[] articleArray = skinnyResponse.getBody();

            return Arrays.asList(articleArray);
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
