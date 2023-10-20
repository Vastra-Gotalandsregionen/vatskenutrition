package se.vgregion.vatskenutrition.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import se.vgregion.vatskenutrition.controller.ArticleController;
import se.vgregion.vatskenutrition.service.JwtTokenFactory;
import se.vgregion.vatskenutrition.util.HttpUtil;

/**
 * @author Patrik Björk
 */
@Configuration
@EnableWebMvc
@EnableScheduling
@ComponentScan(basePackageClasses = {ArticleController.class, HttpUtil.class, JwtTokenFactory.class})
@PropertySource(value = "file://${user.home}/.app/vatskenutrition/application.properties", ignoreResourceNotFound = false)
public class AppConfig {

}
