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

import com.alediaferia.ownpage.auth.role.GuestRole
import com.alediaferia.ownpage.auth.role.ProfileManagerRole
import com.alediaferia.ownpage.config.authorizationserver.clients.GuestClientDetailsService
import com.alediaferia.ownpage.utils.extensions.strings.base64Maybe
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.client.BaseClientDetails
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService
import org.springframework.stereotype.Service

@Service
class CompositeClientDetailsService(
    @Value("\${ownapp.oauth2.client_id}") private val clientId: String,
    @Value("\${ownapp.baseUrl}") private val baseUrl: String,
    private val guestClientDetailsService: GuestClientDetailsService,
    private val passwordEncoder: PasswordEncoder
) : ClientDetailsService {
    private val inMemoryClientDetailsService: InMemoryClientDetailsService by lazy {
        InMemoryClientDetailsService().apply {
            setClientDetailsStore(
                mapOf(clientId to builtInClientDetails)
            )
        }
    }

    var guestClientIdPrefix = DEFAULT_CLIENT_ID_PREFIX

    override fun loadClientByClientId(clientId: String): ClientDetails {
        return if (clientId.base64Maybe) {
            guestClientDetailsService.loadClientByClientId(clientId, guestClientIdPrefix)
        } else {
            inMemoryClientDetailsService.loadClientByClientId(clientId)
        }
    }

    private val builtInScopes = setOf(ProfileManagerRole.identifier, GuestRole.identifier)

    private val builtInClientDetails: ClientDetails =
        BaseClientDetails(
            clientId,
            "",
            builtInScopes.joinToString(","),
            "authorization_code",
            "",
            "$baseUrl/authenticated,$baseUrl/guest/authenticated"
        ).apply {
            clientSecret = passwordEncoder.encode("")
            setAutoApproveScopes(builtInScopes)
            accessTokenValiditySeconds = TWENTY_FOUR_HOURS_SECONDS
        }
}

private const val DEFAULT_CLIENT_ID_PREFIX = "guest-"
private const val TWENTY_FOUR_HOURS_SECONDS = 84_400
