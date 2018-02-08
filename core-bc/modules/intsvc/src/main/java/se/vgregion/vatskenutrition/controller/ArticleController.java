package se.vgregion.vatskenutrition.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.vgregion.vatskenutrition.model.Article;
import se.vgregion.vatskenutrition.service.ArticleService;
import se.vgregion.vatskenutrition.util.HttpUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
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
    @ResponseBody
    public Object getArticles() {

        Object articles = articleService.findAllArticles();

        return articles;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public void update() {
        articleService.update();
    }

    @RequestMapping(value = "/year/{year}", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public Object getArticlesByYear(@PathVariable("year") String year) {
        return articleService.findByYear(year);
    }

    @RequestMapping(value = "/year/currentYear", method = RequestMethod.GET)
    @ResponseBody
    public Object getArticlesByCurrentYear() {
        String currentYear = defaultRevision; // todo

        return articleService.findByYear(currentYear);
    }

    @RequestMapping(value = "/admin/url", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, String>> getAdminUrl() {

        Map<String, String> map = new HashMap<>();

        map.put("url", articleAdminUrl);

        return ResponseEntity.ok(map);
    }

    @RequestMapping(value = "/{articleUuid}", method = RequestMethod.GET)
    @ResponseBody
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

        // todo check which revision it belongs too and if other than default require auth

        return ResponseEntity.ok(article);
    }

}
