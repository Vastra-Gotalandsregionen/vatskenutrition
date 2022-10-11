package se.vgregion.vatskenutrition.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.vgregion.vatskenutrition.model.Article;
import se.vgregion.vatskenutrition.service.ArticleService;
import se.vgregion.vatskenutrition.util.HttpUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private HttpUtil httpUtil;

    @Value("${articleAdminUrl}")
    private String articleAdminUrl;

    @Value("${defaultRevision}")
    private String defaultRevision;

    @Autowired
    private HttpServletRequest request;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Object getArticles() {

        Object articles = articleService.findAllArticles();

        return articles;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public void update() {
        articleService.update();
    }

    @RequestMapping(value = "/year/{year}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public Object getArticlesByYear(@PathVariable("year") String year) {
        return articleService.findByYear(year);
    }

    @RequestMapping(value = "/year/currentYear", method = RequestMethod.GET)
    public Object getArticlesByCurrentYear() {
        String currentYear = defaultRevision;

        return articleService.findByYear(currentYear);
    }

    @RequestMapping(value = "/admin/url", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> getAdminUrl() {

        Map<String, String> map = new HashMap<>();

        map.put("url", articleAdminUrl);

        return ResponseEntity.ok(map);
    }

    @RequestMapping(value = "/startPageArticle/{year}", method = RequestMethod.GET)
    public ResponseEntity<Article> getStartPageArticle(@PathVariable("year") String year) {
        return ResponseEntity.ok(articleService.findStartPageArticle(year));
    }

    @RequestMapping(value = "/startPageArticle/currentYear", method = RequestMethod.GET)
    public ResponseEntity<Article> getStartPageArticleCurrentYear() {
        return ResponseEntity.ok(articleService.findStartPageArticle(defaultRevision));
    }

    @RequestMapping(value = "/{year}/{articleTitle}", method = RequestMethod.GET)
    public ResponseEntity<Article> getArticleByYearAndTitle(@PathVariable("articleTitle") String articleTitle,
                                                            @PathVariable("year") String year) {

        articleTitle = articleTitle.replace("_", " ");

        Article article = articleService.findArticle(articleTitle, year);

        if (article == null) {
            // Fallback to handle old links.
            return getArticleByUuid(articleTitle);
        }

        // If fetching other than default revision authenication is required.
        if (!article.getPaths().get(0).equals(articleService.getDefaultRevision())
                && httpUtil.getUserIdFromRequest(request) == null) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(article);
    }

    @RequestMapping(value = "/{articleUuid}", method = RequestMethod.GET)
    public ResponseEntity<Article> getArticleByUuid(@PathVariable("articleUuid") String articleUuid) {


        Article article = articleService.findArticle(articleUuid);

        if (article == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // If fetching other than default revision authenication is required.
        if (!article.getPaths().get(0).equals(articleService.getDefaultRevision())
                && httpUtil.getUserIdFromRequest(request) == null) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(article);
    }

}
