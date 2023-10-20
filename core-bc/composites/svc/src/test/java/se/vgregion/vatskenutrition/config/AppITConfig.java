package se.vgregion.vatskenutrition.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Patrik Björk
 */
@Configuration
@ComponentScan(basePackages = {"se.vgregion.vatskenutrition.service"})
@PropertySource("classpath:application-IT.properties")
public class AppITConfig {

}
