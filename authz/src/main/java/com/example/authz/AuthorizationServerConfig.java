package com.example.authz;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.filter.ForwardedHeaderFilter;

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

@Configuration
public class AuthorizationServerConfig {

	@Bean
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
		return http.cors(Customizer.withDefaults()).formLogin(Customizer.withDefaults()).build();
	}

	@Bean
	@Order(2)
	SecurityFilterChain appEndpoints(HttpSecurity http) throws Exception {
		// @formatter:off
		http
			.addFilterBefore(new ForwardedHeaderFilter(), LogoutFilter.class)
			.authorizeRequests((authz) -> authz
					.mvcMatchers("/error").permitAll().anyRequest().authenticated())
			.formLogin(Customizer.withDefaults())
			.oauth2ResourceServer((oauth2) -> oauth2
					.jwt(Customizer.withDefaults()));
		return http.build();
		// @formatter:on
	}

	// @formatter:off
	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("pc-client")
				.clientSecret("{noop}secret")
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.redirectUri("http://127.0.0.1:8080/login/oauth2/code/spring")
				.scope("pc.read")
				.scope("pc.write")
				.scope("openid")
				.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
				.build();
		// demo dust to make the resource server step simpler to demonstrate
		RegisteredClient luke = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("luke")
				.clientSecret("{noop}alderaan")
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.redirectUri("http://127.0.0.1:8080")
				.scope("pc.read")
				.scope("pc.write")
				.scope("openid")
				.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
				.build();
		return new InMemoryRegisteredClientRepository(registeredClient, luke);
	}
	// @formatter:on

	@Bean
	public JWKSource<SecurityContext> jwkSource(KeyPair keyPair) {
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		// @formatter:off
		RSAKey rsaKey = new RSAKey.Builder(publicKey)
				.privateKey(privateKey)
				.keyID(UUID.randomUUID().toString())
				.build();
		JWKSet jwkSet = new JWKSet(rsaKey);
		return new ImmutableJWKSet<>(jwkSet);
	}

	@Bean
	public JwtDecoder jwtDecoder(KeyPair keyPair) {
		return NimbusJwtDecoder.withPublicKey((RSAPublicKey) keyPair.getPublic()).build();
	}

	// @formatter:off
	@Bean
	public UserDetailsService users() {
		UserDetails user = User.withDefaultPasswordEncoder()
				.username("luke")
				.password("alderaan")
				.authorities("app")
				.build();
		return new InMemoryUserDetailsManager(user);
	}
	// @formatter:on

	@Bean
	public ProviderSettings providerSettings() {
		return ProviderSettings.builder().issuer("http://idp:8280").build();
	}

	@Bean
	@Role(ROLE_INFRASTRUCTURE)
	KeyPair generateRsaKey() {
		KeyPair keyPair;
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.generateKeyPair();
		}
		catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return keyPair;
	}
}