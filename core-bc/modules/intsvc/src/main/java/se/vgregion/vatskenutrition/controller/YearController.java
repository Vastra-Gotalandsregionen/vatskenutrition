package se.vgregion.vatskenutrition.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.vgregion.vatskenutrition.service.ArticleService;

import java.util.List;

@RestController
@RequestMapping("/year")
public class YearController {

    @Autowired
    private ArticleService articleService;

    @RequestMapping(value = "/currentYear", method = RequestMethod.GET)
    public String getCurrentYear() {
        return articleService.getDefaultRevision();
    }

    @RequestMapping(value = "/availableYears", method = RequestMethod.GET)
//    @PreAuthorize("hasAuthority('ADMIN_USER') or hasAuthority('STANDARD_USER')")
    public List<String> getAvailableYears() {
        return articleService.findAvailableYears();
    }



}
