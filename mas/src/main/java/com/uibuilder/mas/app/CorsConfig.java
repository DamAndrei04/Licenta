package com.uibuilder.mas.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configurația CORS a modulului MAS, care permite cererile provenite din frontend-ul ce
 * rulează pe {@code http://localhost:3000}.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    /**
     * Înregistrează regulile CORS pentru toate rutele: originea permisă, metodele HTTP și
     * antetele acceptate.
     *
     * @param registry registrul de configurare CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
