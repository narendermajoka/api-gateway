/**
 * 
 */
package com.idemia.tfe.config;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtConfig jwtConfig;

	private static final String[] AUTH_WHITELIST = {
			// -- swagger ui
			"/*/v2/api-docs",
			"/*/swagger-resources",
			"/*/swagger-resources/**",
			"/*/configuration/ui",
			"/*/configuration/security",
			"/*/swagger-ui.html",
			"/*/webjars/**"
			// other public endpoints of your API may be appended to this array
	};

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.csrf().disable()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) 	
		.and()
		.exceptionHandling().accessDeniedHandler((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED,e.getMessage()))
		.and()
		.exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED,e.getMessage()))
		.and()
		.addFilterAfter(new JwtTokenAuthenticationFilter(jwtConfig), UsernamePasswordAuthenticationFilter.class)
		.authorizeRequests()
		.antMatchers(AUTH_WHITELIST).permitAll()
		.antMatchers(HttpMethod.POST, jwtConfig.getUri()).permitAll()  
		.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
		// .antMatchers("/echo-service/echo" + "/admin/**").hasRole("ADMIN")
		.anyRequest().authenticated(); 
	}
	/*@Bean
	  CorsConfigurationSource corsConfigurationSource() {
	      CorsConfiguration configuration = new CorsConfiguration();
	      configuration.setAllowedOrigins(Arrays.asList("http://192.168.21.128"));
	     // configuration.setAllowedOrigins(Arrays.asList("*"));
	      configuration.setAllowedMethods(Arrays.asList("GET","POST","OPTIONS"));
	      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	      source.registerCorsConfiguration("/**", configuration);
	      return source;
	  }*/
}
