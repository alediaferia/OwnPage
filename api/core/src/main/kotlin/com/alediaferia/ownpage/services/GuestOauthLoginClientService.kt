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

package com.alediaferia.ownpage.services

import com.alediaferia.ownpage.entities.GuestOauthLoginClient
import com.alediaferia.ownpage.repositories.GuestOauthLoginClientRepository
import com.alediaferia.ownpage.resources.exceptions.GuestOauthClientNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URI
import java.net.URL

@Service
class GuestOauthLoginClientService(
    private val repository: GuestOauthLoginClientRepository,
    private val ownPageRefService: OwnPageRefService
) {
    // TODO standardize uri schemes across instances
    companion object {
        const val DEFAULT_AUTHORIZE_URI = "/api/oauth/authorize"
        const val DEFAULT_TOKEN_URI = "/api/oauth/token"
        const val DEFAULT_USER_INFO_URI = "/api/users/self/info"
        const val DEFAULT_JWK_SET_URI = "/api/.well-known/jwks.json"

        private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    }

    private val idSize = 8

    fun getByRegistrationId(registrationId: String): GuestOauthLoginClient {
        return repository.findByRegistrationId(registrationId)
            ?: throw GuestOauthClientNotFoundException(registrationId)
    }

    @Transactional(readOnly = false)
    fun createOrGet(clientId: String, ownPageBaseURL: URL): GuestOauthLoginClient {
        val ownPageRef = ownPageRefService.getOrCreateByBaseUrl(ownPageBaseURL.toString())

        return repository.findByClientId(clientId)
            ?: repository.save(
                GuestOauthLoginClient(
                    // TODO decouple registrationId from clientId
                    registrationId = clientId,
                    clientId = clientId,
                    authorizationUri = ownPageBaseURL.toAuthorizeUrl(),
                    tokenUri = ownPageBaseURL.toTokenUrl(),
                    userInfoUri = ownPageBaseURL.toUserInfoUrl(),
                    jwkSetUri = ownPageBaseURL.toJwkSetUri(),
                    baseUrl = ownPageBaseURL.toString(),
                    ownPageRef = ownPageRef
                )
            )
    }

    private fun URL.toAuthorizeUrl(): String =
        withUri(DEFAULT_AUTHORIZE_URI)

    private fun URL.toTokenUrl(): String =
        withUri(DEFAULT_TOKEN_URI)

    private fun URL.toUserInfoUrl(): String =
        withUri(DEFAULT_USER_INFO_URI)

    private fun URL.toJwkSetUri(): String =
        withUri(DEFAULT_JWK_SET_URI)

    private fun URL.withUri(uri: String): String = URI(
        protocol,
        null,
        host,
        port,
        uri,
        null,
        null
    ).toURL().toString()

    private fun generateRegistrationId(): String {
        var identifier: String
        do {
            identifier = generateIdentifier()
        } while (repository.existsByRegistrationId(identifier))
        return identifier
    }

    private fun generateIdentifier(): String {
        return (1..idSize).asSequence()
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}
