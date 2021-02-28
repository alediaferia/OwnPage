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

package com.alediaferia.ownpage.resources.validation

import com.alediaferia.ownpage.components.GuestClientIdFactory
import org.springframework.stereotype.Service
import java.net.MalformedURLException
import java.net.URI
import java.net.URL

@Service
class GuestOauthLoginClientValidator(
    private val guestClientIdFactory: GuestClientIdFactory
    ) {
    class InvalidGuestClientIdException : RuntimeException {
        constructor(clientId: String, cause: Throwable) : super("The specified client id is an invalid guest client id ($clientId)", cause)
        constructor(clientId: String) : super("The specified client id is an invalid guest client id ($clientId)")
    }

    data class GuestLoginClientIdInfo(
        val encodedClientId: String,
        val guestUrl: URL
    )

    fun validateGuestAuthServer(serverUrl: String): GuestLoginClientIdInfo {
        return try {
            val sanitizedUrl = URL(serverUrl).sanitized()
            GuestLoginClientIdInfo(
                guestClientIdFactory.generateClientId(sanitizedUrl),
                sanitizedUrl
            )
        } catch (ex: MalformedURLException) {
            throw InvalidGuestClientIdException(serverUrl, ex)
        }
    }

    private fun URL.sanitized(): URL {
        return URI(
                protocol,
                null,
                host,
                port,
                null,
                null,
                null
        ).toURL()
    }
}
