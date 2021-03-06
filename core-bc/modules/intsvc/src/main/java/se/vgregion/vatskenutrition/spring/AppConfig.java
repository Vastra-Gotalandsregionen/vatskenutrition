package se.vgregion.vatskenutrition.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import se.vgregion.vatskenutrition.controller.ArticleController;
import se.vgregion.vatskenutrition.repository.ArticleRepository;
import se.vgregion.vatskenutrition.service.ArticleService;
import se.vgregion.vatskenutrition.util.HttpUtil;

import javax.sql.DataSource;

/**
 * @author Patrik Björk
 */
@Configuration
@EnableWebMvc
@EnableScheduling
@ComponentScan(basePackageClasses = {ArticleController.class, HttpUtil.class, ArticleService.class})
@PropertySource(value = "file://${user.home}/.app/vatskenutrition/application.properties", ignoreResourceNotFound = false)
@EnableJpaRepositories(basePackageClasses = {ArticleRepository.class})
public class AppConfig {

    @Bean
    public DataSource dataSource() {

        // no need shutdown, EmbeddedDatabaseFactoryBean will take care of this
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        EmbeddedDatabase db = builder
                .setType(EmbeddedDatabaseType.H2) //.H2 or .DERBY
//                .addScript("db/sql/create-db.sql")
//                .addScript("db/sql/insert-data.sql")
                .build();
        return db;
    }
}
