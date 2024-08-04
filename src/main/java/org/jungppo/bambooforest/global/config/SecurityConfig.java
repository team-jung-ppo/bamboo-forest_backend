package org.jungppo.bambooforest.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.global.jwt.domain.JwtProvider;
import org.jungppo.bambooforest.global.jwt.presentation.CustomAccessDeniedHandler;
import org.jungppo.bambooforest.global.jwt.presentation.CustomAuthenticationEntryPoint;
import org.jungppo.bambooforest.global.jwt.presentation.JwtAuthenticationFilter;
import org.jungppo.bambooforest.global.oauth2.presentation.CustomOAuth2LoginFailureHandler;
import org.jungppo.bambooforest.global.oauth2.presentation.CustomOAuth2LoginSuccessHandler;
import org.jungppo.bambooforest.global.oauth2.service.CustomJdbcOAuth2AuthorizedClientService;
import org.jungppo.bambooforest.global.oauth2.service.CustomOAuth2UserService;
import org.jungppo.bambooforest.global.oauth2.service.HttpCookieOAuth2AuthorizationRequestRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2LoginSuccessHandler customOauth2LoginSuccessHandler;
    private final CustomOAuth2LoginFailureHandler customOauth2LoginFailureHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager,
                                           CustomJdbcOAuth2AuthorizedClientService customJdbcOAuth2AuthorizedClientService)
            throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .requestCache(RequestCacheConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/status/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/members/reissuance").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(new JwtAuthenticationFilter(authenticationManager, objectMapper),
                        UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(configure -> configure
                        .authorizationEndpoint(
                                config -> config.authorizationRequestRepository(
                                        httpCookieOAuth2AuthorizationRequestRepository))
                        .userInfoEndpoint(config -> config.userService(customOAuth2UserService))
                        .authorizedClientService(customJdbcOAuth2AuthorizedClientService)
                        .successHandler(customOauth2LoginSuccessHandler)
                        .failureHandler(customOauth2LoginFailureHandler)
                )
                .exceptionHandling((exceptionConfig) ->
                        exceptionConfig.authenticationEntryPoint(customAuthenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler)
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(JwtProvider jwtProvider) {
        return new ProviderManager(jwtProvider);
    }

    @Bean
    public CustomJdbcOAuth2AuthorizedClientService customJdbcOAuth2AuthorizedClientService(JdbcTemplate jdbcTemplate,
                                                                                           ClientRegistrationRepository clientRegistrationRepository) {
        return new CustomJdbcOAuth2AuthorizedClientService(jdbcTemplate, clientRegistrationRepository);
    }
}
