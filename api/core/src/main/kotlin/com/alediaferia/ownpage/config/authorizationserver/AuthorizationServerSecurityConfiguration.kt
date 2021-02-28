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

package com.alediaferia.ownpage.config.authorizationserver

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.web.cors.CorsConfiguration
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration as SpringAuthorizationServerSecurityConfiguration

@Configuration
class AuthorizationServerSecurityConfiguration : SpringAuthorizationServerSecurityConfiguration() {
    override fun configure(http: HttpSecurity) {
        super.configure(http)

        http.cors {
            it.configurationSource {
                CorsConfiguration().apply {
                    allowedOrigins = listOf("*")
                    allowedMethods = listOf(
                            HttpMethod.POST.name
                    )
                    allowedHeaders = listOf(
                            HttpHeaders.CONTENT_TYPE,
                            HttpHeaders.ACCEPT
                    )
                }
            }
        }
    }
}
