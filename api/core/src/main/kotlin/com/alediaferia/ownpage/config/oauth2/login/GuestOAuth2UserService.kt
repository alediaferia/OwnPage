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

package com.alediaferia.ownpage.config.oauth2.login

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import java.net.URI

class GuestOAuth2UserService : OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private val defaultOAuth2UserService = DefaultOAuth2UserService()

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oauth2User = defaultOAuth2UserService.loadUser(userRequest)

        val clientRegistration = userRequest.clientRegistration
        val baseUrl = with(clientRegistration.providerDetails.userInfoEndpoint) {
            val userInfoUri = URI(uri)
            URI(
                    userInfoUri.scheme,
                    null,
                    userInfoUri.host,
                    userInfoUri.port,
                    null,
                    null,
                    null
            ).toURL()
        }

        val instanceName = baseUrl.host

        return GuestOAuth2User(
                "${oauth2User.name}@$instanceName",
                oauth2User.attributes + mapOf(
                        GuestOAuth2User.BASE_URL_ATTRIBUTE_KEY to baseUrl,
                        GuestOAuth2User.CLIENT_REGISTRATION_ID_ATTRIBUTE_KEY to clientRegistration.registrationId
                )
        )
    }
}
