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

import com.alediaferia.ownpage.repositories.GuestOauthLoginClientRepository
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.stereotype.Component

@Component
class GuestClientRegistrationRepository(
        private val guestOauthLoginClientRepository: GuestOauthLoginClientRepository
) : ClientRegistrationRepository {
    companion object {
        private const val USERNAME_ATTRIBUTE_NAME = "name"
    }

    override fun findByRegistrationId(registrationId: String): ClientRegistration? {
        return guestOauthLoginClientRepository.findByRegistrationId(registrationId)?.let {
            ClientRegistration.withRegistrationId(registrationId)
                    .clientId(it.clientId)
                    .clientSecret(it.clientSecret)
                    .scope(it.scopes)
                    .authorizationGrantType(AUTHORIZATION_CODE) // hard-coded for now
                    .redirectUriTemplate(it.redirectUriTemplate)
                    .tokenUri(it.tokenUri)
                    .authorizationUri(it.authorizationUri)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
                    .userInfoUri(it.userInfoUri)
                    .userNameAttributeName(USERNAME_ATTRIBUTE_NAME)
                    .jwkSetUri(it.jwkSetUri)
                    .build()
        }
    }
}
