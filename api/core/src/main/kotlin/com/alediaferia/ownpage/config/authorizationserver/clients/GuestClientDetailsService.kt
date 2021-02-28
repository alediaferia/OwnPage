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

package com.alediaferia.ownpage.config.authorizationserver.clients

import com.alediaferia.ownpage.auth.role.GuestRole
import com.alediaferia.ownpage.components.GuestClientIdFactory
import com.alediaferia.ownpage.utils.extensions.strings.base64Decoded
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.ClientRegistrationException
import org.springframework.stereotype.Service
import java.net.MalformedURLException
import java.net.URL

@Service
class GuestClientDetailsService(
    private val passwordEncoder: PasswordEncoder,
    private val guestClientIdFactory: GuestClientIdFactory,
    @Value("\${ownapp.baseUrl}")
    private val baseUrl: URL
) {
    private val clientsCache: MutableMap<URL, ClientDetails> = mutableMapOf()

    fun loadClientByClientId(clientId: String, prefix: String): ClientDetails {
        val guestClientId = guestClientIdFactory.decodeClientId(clientId)
        if (guestClientId.identityOwnerOwnPageUrl != baseUrl)
            throw ForeignClientIdError

        return clientsCache.getOrPut(guestClientId.callbackBaseUrl) {
            OwnPageClientDetails(clientId, guestClientId.callbackBaseUrl, setOf(GuestRole.identifier), passwordEncoder.encode(""))
        }
    }
}

sealed class GuestClientIdError : ClientRegistrationException("The guest client ID is invalid")
object ClientURLAsClientIdRequredError : GuestClientIdError()
object InvalidClientIDPrefixError : GuestClientIdError()
object ForeignClientIdError : GuestClientIdError()
