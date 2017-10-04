package se.vgregion.vatskenutrition.repository.extended;

import org.springframework.beans.factory.annotation.Autowired;
import se.vgregion.vatskenutrition.model.Article;
import se.vgregion.vatskenutrition.repository.ArticleRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    @Autowired
    private ArticleRepository articleRepository;

    @Override
    public List<Article> findArticles(String year, Integer... status) {

        List<Article> articles = articleRepository.findAll();

        return articles.stream()
                .filter(a -> a.getPaths().contains(year)
                        && (status == null || status.length == 0 || Arrays.asList(status).contains(a.getStatus())))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findAvailableYears() {
        List<Article> all = articleRepository.findAll();

        Set<String> years = new TreeSet<>();
        for (Article article : all) {
            if (article.getPaths() != null && article.getPaths().size() > 0) {
                years.add(article.getPaths().get(0));
            }
        }

        return new ArrayList<>(years);
    }

}
