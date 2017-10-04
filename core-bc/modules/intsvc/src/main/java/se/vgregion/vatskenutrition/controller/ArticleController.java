package se.vgregion.vatskenutrition.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import se.vgregion.vatskenutrition.model.Article;
import se.vgregion.vatskenutrition.service.ArticleService;

@Controller
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public Object getArticles() {

        Object articles = articleService.findAllArticles();

        return articles;
    }

    @RequestMapping(value = "/year/{year}", method = RequestMethod.GET)
    @ResponseBody
    public Object getArticlesByYear(@PathVariable("year") String year,
                                    @RequestParam(name = "includeDrafts", required = false) Boolean includeDrafts) {

        return articleService.findByYear(year, includeDrafts);
    }

    @RequestMapping(value = "/year/currentYear", method = RequestMethod.GET)
    @ResponseBody
    public Object getArticlesByCurrentYear(@PathVariable(name = "includeDrafts", required = false) Boolean includeDrafts) {
        String currentYear = "2017"; // todo

        return articleService.findByYear(currentYear, includeDrafts);
    }

    @RequestMapping(value = "/{articleUuid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Article> getArticleByUuid(@PathVariable("articleUuid") String articleUuid) {


        Article article = articleService.findArticle(articleUuid);

        if (article == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(article);
    }

}
