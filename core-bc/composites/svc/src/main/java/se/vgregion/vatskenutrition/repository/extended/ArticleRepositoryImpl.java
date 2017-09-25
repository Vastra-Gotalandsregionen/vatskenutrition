package se.vgregion.vatskenutrition.repository.extended;

import org.springframework.beans.factory.annotation.Autowired;
import se.vgregion.vatskenutrition.model.Article;
import se.vgregion.vatskenutrition.repository.ArticleRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ArticleRepository articleRepository;

    @Override
    public List<Article> findArticles(String year) {
//        Query query = entityManager.createQuery("select distinct a from Article a left join fetch a.paths");

//        List<Article> resultList = query.getResultList();

        List<Article> articles = articleRepository.findAll();

        return articles.stream()
                .filter(article -> article.getPaths().contains(year))
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
        /*Set<String> reduce = all.stream().reduce(new TreeSet<>(), (Set<String> paths, Article article) -> {
            if (article.getPaths() != null && article.getPaths().size() > 0) {
                paths.add(article.getPaths().get(0));
            }
            return paths;
        }, ((Set<String> set1, Set<String> set2) -> {
            set1.addAll(set2);
            return set1;
        }));*/

        return new ArrayList<>(years);
    }
}
