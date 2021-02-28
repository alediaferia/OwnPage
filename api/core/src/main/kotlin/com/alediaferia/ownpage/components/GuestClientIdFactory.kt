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

package com.alediaferia.ownpage.components

import com.alediaferia.ownpage.utils.extensions.strings.base64Decoded
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.MalformedURLException
import java.net.URL
import java.util.*

@Component
class GuestClientIdFactory(
    @Value("\${ownapp.baseUrl}")
    private val baseUrl: String
) {
    private val base64Encoder = Base64.getUrlEncoder()
    companion object {
        private const val URL_SEPARATOR = "||"
        private const val GUEST_CLIENT_ID_SEPARATOR = "guest-"
    }

    data class GuestClientId(
        val callbackBaseUrl: URL,
        val identityOwnerOwnPageUrl: URL
    )

    fun generateClientId(
        identityOwnerOwnPageUrl: URL
    ): String {
        return base64Encoder.encodeToString("$GUEST_CLIENT_ID_SEPARATOR$baseUrl$URL_SEPARATOR$identityOwnerOwnPageUrl".toByteArray())
    }

    @Throws(MalformedURLException::class)
    fun decodeClientId(clientId: String): GuestClientId {
        val decodedClientId = clientId.base64Decoded() ?: throw InvalidGuestClientId(clientId)
        val firstPart = if (!decodedClientId.startsWith(GUEST_CLIENT_ID_SEPARATOR))
            throw InvalidGuestClientId(clientId)
        else
            decodedClientId.substring(GUEST_CLIENT_ID_SEPARATOR.length)

        return firstPart.split(URL_SEPARATOR).let {
            if (it.size != 2)
                throw InvalidGuestClientId(clientId)
            else
                GuestClientId(
                    URL(it[0]),
                    URL(it[1])
                )
        }
    }
}

class InvalidGuestClientId(clientId: String) : RuntimeException("The provided client id is invalid or malformed: '$clientId'")
