package com.example.petapp.infrastructure.database.elasticsearch;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration(proxyBeanMethods = false)
@EnableElasticsearchRepositories(
        basePackages = "com.example.petapp.infrastructure.database.elasticsearch"
)
public class ElasticConfig {
}
