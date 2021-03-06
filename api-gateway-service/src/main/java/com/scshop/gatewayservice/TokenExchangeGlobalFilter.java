package com.scshop.gatewayservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class TokenExchangeGlobalFilter implements GlobalFilter {

	final Logger logger = LoggerFactory.getLogger(TokenExchangeGlobalFilter.class);

	@Autowired
	WebClient.Builder webClientBuilder;
	
	@Autowired
	JwtDecoder jwtDecoder;

	@Value("${application.security.authorizationServer.host}")
	private String AUTHORIZATION_SERVER_HOST; 
	
	@Value("${application.security.authorizationServer.port}")
	private Integer AUTHORIZATION_SERVER_PORT; 
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		
		logger.info("\n\n Inside..... ");
		System.out.println("\n\n Inside..... ");
		
		String bearerToken = exchange.getRequest().getHeaders().getFirst("Authorization");
		if (bearerToken == null || bearerToken.isEmpty()) {
			return chain.filter(exchange);
		}

		Jwt jwt = null;
		String clientId = null;
		String accessToken = null;
		String newScope = "product"; //cart order payment
		
		try {
			logger.info("\n\n Exchanging token..... bearerToken:" + bearerToken);
			System.out.println("\n\n Exchanging token..... bearerToken:" + bearerToken);
	
			accessToken = bearerToken.substring(7, bearerToken.length());
			logger.info("\n\n Exchanging token..... accessToken: " + accessToken);
			System.out.println("\n\n Exchanging token..... accessToken: " + accessToken);

			jwt = jwtDecoder.decode(accessToken);
			logger.info("\n\n Exchanging token..... jwt: " + jwt);
			System.out.println("\n\n Exchanging token..... jwt: " + jwt);

			clientId = (String) jwt.getClaims().get("azp");
			logger.info("\n\n Exchanging token..... clientId: " + clientId);
			System.out.println("\n\n Exchanging token..... clientId: " + clientId);
			
	
			logger.info("\n\n  Exchanging token..... for: " + clientId);
			System.out.println("\n\n  Exchanging token..... for: " + clientId);
			
		}catch(Exception ex) {
			logger.error("Exception: " + ex);
			ex.printStackTrace();
		}

		return webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.build().post()
				.uri(uriBuilder -> uriBuilder.scheme("http").host(AUTHORIZATION_SERVER_HOST).port(AUTHORIZATION_SERVER_PORT)
						.path("/auth/realms/sc-shop/protocol/openid-connect/token").build())

				.body(BodyInserters.fromFormData("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange")
						.with("subject_token", accessToken)
						.with("client_id", clientId)
						.with("scope", newScope))
				
				.retrieve().bodyToMono(AccessToken.class).flatMap(s -> {
					logger.info("\n\n Exchanged Access Token: " + s.getAccess_token());
					System.out.println("\n\n Exchanged Access Token: " + s.getAccess_token());

					String authorizationHeaderWithExchangedToken = "Bearer " + s.getAccess_token();
					exchange.getRequest().mutate().header("Authorization", authorizationHeaderWithExchangedToken);

					return chain.filter(exchange);
				});
	}

}

class AccessToken {

	private String access_token;

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
}