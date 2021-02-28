/*
 * OwnPage
 * Copyright (C) 2021 Alessandro Diaferia
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("unused")

package com.alediaferia.ownpage.config

import com.alediaferia.ownpage.auth.CreateProfileAuthority
import com.alediaferia.ownpage.auth.OWNER_ROLE_NAME
import com.alediaferia.ownpage.auth.PublishCommentAuthority
import com.alediaferia.ownpage.auth.PublishPostAuthority
import com.alediaferia.ownpage.auth.jwt.JwtGrantedAuthoritiesConverter
import com.alediaferia.ownpage.auth.role.GuestRole
import com.alediaferia.ownpage.auth.role.InMemoryRoleProvider
import com.alediaferia.ownpage.auth.role.ProfileManagerRole
import com.alediaferia.ownpage.auth.role.RoleProvider
import com.alediaferia.ownpage.config.oauth2.login.GuestClientRegistrationRepository
import com.alediaferia.ownpage.config.oauth2.login.GuestOAuth2UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.web.reactive.function.client.WebClient
import java.net.URLEncoder
import java.security.KeyPair
import java.security.interfaces.RSAPublicKey


@Configuration
@EnableWebSecurity
@Order(1)
class SecurityConfiguration(
    @Value("\${ownapp.baseUrl}") private val baseUrl: String,
    @Value("\${ownapp.oauth2.client_id}") private val clientId: String,
    @Value("\${server.servlet.context-path}") private val servletContextPath: String,
    private val guestClientRegistrationRepository: GuestClientRegistrationRepository,
    private val keyPair: KeyPair
) : WebSecurityConfigurerAdapter() {

    // TODO: consider splitting into multiple configurations
    // https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#multiple-httpsecurity
    override fun configure(http: HttpSecurity) {
        val roleProvider = applicationContext.getBean(RoleProvider::class.java)

        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/login").permitAll()
            .antMatchers("/profile/register").hasAuthority(CreateProfileAuthority.authority)
            .antMatchers(GET, "/posts").permitAll()
            .antMatchers(GET, "/posts/{postId}").permitAll()
            .antMatchers(POST, "/posts").hasAuthority(PublishPostAuthority.authority)
            .antMatchers(POST, "/posts/{postId}/comments").hasAuthority(PublishCommentAuthority.authority)
            .antMatchers(POST, "/postrefs").hasAuthority(PublishCommentAuthority.authority)
            .antMatchers(GET, "/postrefs/{postrefId}/comments").permitAll()
            .antMatchers(POST, "/postrefs/{postrefId}/comments").hasAuthority(PublishCommentAuthority.authority)
            .antMatchers(GET, "/remotecomments/{id}").permitAll()
            .antMatchers(POST, "/users/admin").hasRole(OWNER_ROLE_NAME)
            .antMatchers(POST, "/oauth/guest/validate").permitAll()
            .antMatchers(GET, "/.well-known/jwks.json").permitAll()
            .anyRequest().authenticated()
            .and().httpBasic()
            .and().formLogin {
                it
                    .loginPage("$baseUrl/login")
                    .loginProcessingUrl("/perform_login")
                    .defaultSuccessUrl("$baseUrl/")
                    .failureUrl("$baseUrl/login?error=true")

            }
            .oauth2Login {
                // little trick here to trigger the oauth2 authorization code flow
                // as soon as the guest oauth2 login is successful: this will mean that the ui
                // will receive the access token at the end of this flow
                it.defaultSuccessUrl(guestAuthorizeUrl)

                it.clientRegistrationRepository(guestClientRegistrationRepository)
                it.userInfoEndpoint { config ->
                    config.userAuthoritiesMapper {
                        listOf(PublishCommentAuthority)
                    }
                    config.userService(GuestOAuth2UserService())
                }
            }
            .oauth2ResourceServer().jwt {
                it.decoder(NimbusJwtDecoder.withPublicKey(keyPair.public as RSAPublicKey).build())
                    .jwtAuthenticationConverter(JwtAuthenticationConverter().apply {
                        setJwtGrantedAuthoritiesConverter(JwtGrantedAuthoritiesConverter(roleProvider))
                    })
            }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun roleProvider(): RoleProvider {
        return InMemoryRoleProvider(
            mapOf(
                ProfileManagerRole.identifier to ProfileManagerRole,
                GuestRole.identifier to GuestRole
            )
        )
    }

    @Bean
    fun authorizedClientManager(
        clientRegistrationRepository: ClientRegistrationRepository,
        authorizedClientRepository: OAuth2AuthorizedClientRepository
    ): OAuth2AuthorizedClientManager {
        val authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
            .authorizationCode()
            .refreshToken()
            .build()

        val authorizedClientManager = DefaultOAuth2AuthorizedClientManager(
            guestClientRegistrationRepository, authorizedClientRepository
        )
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider)
        return authorizedClientManager
    }

    @Bean("oauth2WebClient")
    fun webClient(authorizedClientManager: OAuth2AuthorizedClientManager): WebClient {
        val oauth2Client = ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager)
        return WebClient.builder()
            .apply(oauth2Client.oauth2Configuration())
            .build()
    }

    private val guestAuthorizeUrl =
        "/oauth/authorize?response_type=code&client_id=$clientId&scope=guest&redirect_uri=${URLEncoder.encode("$baseUrl/guest/authenticated", "UTF-8")}"
}
