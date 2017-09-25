package se.vgregion.vatskenutrition.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.vgregion.vatskenutrition.service.ArticleService;

import java.util.List;

@Controller
@RequestMapping("/year")
public class YearController {

    @Autowired
    private ArticleService articleService;

    @RequestMapping(value = "/currentYear", method = RequestMethod.GET)
    @ResponseBody
    public String getCurrentYear() {

        String currentYear = "2017"; // todo

        /*List<ArticleDTO> articleDTOs = articles.stream().map(journalArticle -> {
            ArticleDTO articleDTO = new ArticleDTO();

            journalArticle.getTitle();
            journalArticle.getContent();
            articleDTO.setTitle(journalArticle.getTitle());
            articleDTO.setContent(journalArticle.getContent());

            return articleDTO;
        }).collect(Collectors.toList());*/
        return currentYear;
    }

    @RequestMapping(value = "/availableYears", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getAvailableYears() {
        return articleService.findAvailableYears();
    }



}
