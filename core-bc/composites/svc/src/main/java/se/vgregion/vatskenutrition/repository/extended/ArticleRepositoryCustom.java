package se.vgregion.vatskenutrition.repository.extended;

import se.vgregion.vatskenutrition.model.Article;

import java.util.List;

public interface ArticleRepositoryCustom {

    List<Article> findArticles(String year);

    List<String> findAvailableYears();
}
