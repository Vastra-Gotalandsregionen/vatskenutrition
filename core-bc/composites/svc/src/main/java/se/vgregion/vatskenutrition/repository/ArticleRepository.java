package se.vgregion.vatskenutrition.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.vgregion.vatskenutrition.model.Article;
import se.vgregion.vatskenutrition.repository.extended.ArticleRepositoryCustom;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, String>, ArticleRepositoryCustom {

//    List<Article> findAllByPathsIsNotNull();

//    @Query("select distinct a from Article a")
    @Query("select distinct a from Article a left join fetch a.paths order by a.title")
//    @Query("select distinct a from Article a l/**/eft join fetch a.paths left join fetch a.fields")
    List<Article> findAll();

    @Query("select distinct a from Article a left join fetch a.paths where a.uuid=:uuid")
    Article findOne(@Param("uuid") String uuid);

}
