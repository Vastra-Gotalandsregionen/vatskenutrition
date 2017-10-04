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
    private String fetchAllArticlesUrl;// = "http://localhost:9080/api/jsonws/skinny-web.skinny/get-skinny-journal-articles/company-id/10136/group-name/vatskenutrition/ddm-structure-id/1687613/locale/sv_SE/includeDraft/true";

    //    @PostConstruct
    @Scheduled(fixedRate = 5_000)
    public synchronized void update() {
        List<Article> articles = fetchArticlesFromExternalSource();
        articleRepository.deleteAll();
        articleRepository.save(articles);
    }

    public List<Article> findAllArticles() {
        return articleRepository.findAll();
    }

    public List<Article> findByYear(String year, Boolean includeDrafts) {

        includeDrafts = Boolean.TRUE.equals(includeDrafts);
        if (!includeDrafts) {
            return articleRepository.findArticles(year, 0);
        } else {
            return articleRepository.findArticles(year, 0, 2);
        }
    }

    public List<String> findAvailableYears() {
        return articleRepository.findAvailableYears();
    }

    public List<Article> fetchArticlesFromExternalSource() {

        try {

            RestTemplate skinnyTemplate = new RestTemplate();

            ResponseEntity<Article[]> skinnyResponse = skinnyTemplate.getForEntity(
                    fetchAllArticlesUrl,
                    Article[].class, new HashMap<>());


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

        List<Field> fields = article.getFields();
        for (Field field : fields) {
            for (Child child : field.getChildren()) {
                if (child.getName().equalsIgnoreCase("image") && !StringUtils.isEmpty(child.getValue())) {
                    // todo Is this verified in the coming image request? Does it need to?
                    child.setValue(child.getValue() + "&token=secret"); // todo make HMAC
                }
            }
        }

        return article;
    }
}
