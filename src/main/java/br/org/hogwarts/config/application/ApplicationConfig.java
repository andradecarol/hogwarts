package br.org.hogwarts.config.application;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ApplicationConfig {

    @Value("${application.settings.max-result-per-query}")
    private Integer maxResultsPerPage;

}
