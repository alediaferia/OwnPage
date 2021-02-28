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
package com.alediaferia.ownpage.config.authorizationserver

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter

// Authorization server configuration: note that this is achieved using an explicit @Import
// instead of the canonical @EnableAuthorizationServer: this is because, in order to customize
// the cors settings for the authorization server we've had to extend the default AuthorizationServerSecurityConfiguration
// with our own as no other means of configuring HttpSecurity for it are available
@Configuration
@Import(value = [
    AuthorizationServerEndpointsConfiguration::class,
    AuthorizationServerSecurityConfiguration::class
])
class AuthorizationServerConfiguration(
    private val compositeClientDetailsService: CompositeClientDetailsService,
    private val authenticationManager: AuthenticationManager,
    private val jwtAccessTokenConverter: JwtAccessTokenConverter,
    private val tokenStore: TokenStore
) : AuthorizationServerConfigurerAdapter(), ApplicationContextAware {
    private lateinit var _applicationContext: ApplicationContext

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients
            .withClientDetails(compositeClientDetailsService)
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        val userAuthenticationConverter = _applicationContext.getBean(UserAuthenticationConverter::class.java)
        val tokenConverter = jwtAccessTokenConverter.accessTokenConverter as DefaultAccessTokenConverter
        tokenConverter.setUserTokenConverter(userAuthenticationConverter)

        endpoints
            .authenticationManager(authenticationManager) // required for the password grant type
            .accessTokenConverter(jwtAccessTokenConverter)
            .tokenStore(tokenStore)
    }

    override fun configure(security: AuthorizationServerSecurityConfigurer) {
        security.tokenKeyAccess("permitAll()")
            .checkTokenAccess("isAuthenticated()")
            .allowFormAuthenticationForClients()
    }

    @Bean
    fun userAuthenticationConverter(userDetailsService: UserDetailsService): UserAuthenticationConverter {
        return OwnUserAuthenticationConverter(userDetailsService)
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        _applicationContext = applicationContext
    }
}
