package se.vgregion.vatskenutrition.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
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

    //    @PostConstruct
    @Scheduled(fixedRate = 5_000)
    public synchronized void update() {
        LOGGER.info("Updating articles...");
        List<Article> articles = fetchArticlesFromExternalSource();
        articleRepository.deleteAll();
        articleRepository.save(articles);
        LOGGER.info("Finished updating articles...");
    }

    public List<Article> findAllArticles() {
//        update();
        return articleRepository.findAll();
    }

    public List<Article> findByYear(String year) {
//        update();
        return articleRepository.findArticles(year);
    }

    public List<String> findAvailableYears() {
//        update();
        return articleRepository.findAvailableYears();
    }

    public List<Article> fetchArticlesFromExternalSource() {

        try {

//            ResponseEntity<Object> objectResponseEntity = restTemplate.getForEntity("http://localhost:9080/api/jsonws/journalarticle/get-articles/group-id/1687606/folder-id/1688208", "?groupId=1687606&folderId=1688208", Object.class, new HashMap<>());

            RestTemplate skinnyTemplate = new RestTemplate();

       /*     ResponseEntity<Object> skinnyResponse = skinnyTemplate.getForEntity(
                    "http://localhost:9080/api/jsonws/skinny-web.skinny/get-skinny-journal-articles/company-id/10136/group-name/vatskenutrition/ddm-structure-id/1687613/locale/sv_SE",
                    Object.class, new HashMap<>());


            Object body = skinnyResponse.getBody();

            List<Map<String, Object>> articles = (List<Map<String, Object>>) body;

            for (Map<String, Object> article : articles) {

                List<Map<String, List<Map<String, Object>>>> fields = (List<Map<String, List<Map<String, Object>>>>) article.get("fields");
                for (Map<String, List<Map<String, Object>>> field : fields) {

                    List<Map<String, Object>> children = field.get("children");
                    for (Map<String, Object> child : children) {
                        child.get("children"); // We don't have this many levels so should be empty?
                        String name = (String) child.get("name");
                        String html = (String) child.get("value");
                    }

                    String name = (String) (Object) field.get("name");
                }

                List<String> paths = (List<String>) article.get("path");
            }*/

            ResponseEntity<Article[]> skinnyResponse = skinnyTemplate.getForEntity(
                    "http://localhost:9080/api/jsonws/skinny-web.skinny/get-skinny-journal-articles/company-id/10136/group-name/vatskenutrition/ddm-structure-id/1687613/locale/sv_SE/includeDraft/true",
                    Article[].class, new HashMap<>());


            /*Article[] body = skinnyResponse.getBody();

            Article[] articles = body;*/
/*
            for (Article article : articles) {

                Field[] fields = article.fields;
                for (Field field : fields) {
                    Child[] children = field.children;
                    String name = field.name;
                    String value = field.value;
                }

                List<String> paths = article.paths;

                String title = article.title;

            }*/

            Article[] articleArray = skinnyResponse.getBody();
            return Arrays.asList(articleArray);
//            URL url = new URL("http://localhost:9080/api/jsonws/journalarticle/get-articles/group-id/1687606/folder-id/1688208");

//            return JournalArticleLocalServiceUtil.getStructureArticles(1687606L, "1687612");
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
//        update();
        Article article = articleRepository.findOne(articleUuid);

        if (article == null) {
            return null;
        }

        List<Field> fields = article.getFields();
        for (Field field : fields) {
            for (Child child : field.getChildren()) {
                if (child.getName().equalsIgnoreCase("image") && !StringUtils.isEmpty(child.getValue())) {
                    child.setValue(child.getValue() + "&token=secret"); // todo make HMAC
                }
            }
        }

        return article;
    }
}
