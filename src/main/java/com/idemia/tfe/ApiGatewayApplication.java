package com.idemia.tfe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.google.common.collect.ImmutableList;

@EnableDiscoveryClient
@EnableZuulProxy
@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}
	
	@Bean
	public CorsFilter corsFilter() {
	    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    final CorsConfiguration config = new CorsConfiguration();
	    //config.setAllowCredentials(true);
	    config.setAllowedOrigins(ImmutableList.of("*"));
	    config.setAllowedMethods((ImmutableList.of("HEAD", "POST", "PUT", "GET", "PATCH", "DELETE")));
	    config.setAllowedHeaders((ImmutableList.of("Authorization", "Cache-control", "Content-Type")));
	    config.setExposedHeaders((ImmutableList.of("Authorization", "Cache-control", "Content-Type")));
	    config.setMaxAge(3600L);
	    source.registerCorsConfiguration("/**", config);
	    return new CorsFilter(source);
	}
}

