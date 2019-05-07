
package com.idemia.tfe.config;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

	private final JwtConfig jwtConfig;

	public JwtTokenAuthenticationFilter(JwtConfig jwtConfig) {
		this.jwtConfig = jwtConfig;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

/*		response.setHeader("Access-Control-Allow-Origin","*");
		response.setHeader("Access-Control-Allow-Methods","POST, GET, PUT, OPTIONS, DELETE, PATCH");
		response.setHeader("Access-Control-Max-Age","3600");
		response.setHeader("Access-Control-Allow-Headers","authorization, Content-Type");
		response.setHeader("Access-Control-Expose-Headers","authorization, Content-Type");

		if(request.getMethod().equals("OPTIONS")) {
			
			try {
				response.getWriter().print("OK");
				response.getWriter().flush();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}else {*/
			String header = request.getHeader(jwtConfig.getHeader());
			
			if (header == null || !header.startsWith(jwtConfig.getPrefix())) {
				chain.doFilter(request, response); 
				return;
			}
			String token = header.replace(jwtConfig.getPrefix(), "");
			try {
				SecurityContextHolder.getContext().setAuthentication(extractUserFromToken(token));
			} catch (Exception e) {
				SecurityContextHolder.clearContext();
			}
			chain.doFilter(request, response);
		//}
	}

	private UsernamePasswordAuthenticationToken extractUserFromToken(String token) {

		Claims claims = Jwts.parser().setSigningKey(jwtConfig.getSecret().getBytes()).parseClaimsJws(token).getBody();
		String username = claims.getSubject();
		UsernamePasswordAuthenticationToken auth = null;
		if (username != null) {
			@SuppressWarnings("unchecked")
			List<String> authorities = (List<String>) claims.get("authorities");
			auth = new UsernamePasswordAuthenticationToken(username, null,
					authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
		}
		return auth;
	}
}
