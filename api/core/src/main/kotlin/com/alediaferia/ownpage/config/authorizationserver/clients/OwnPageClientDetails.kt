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

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.provider.ClientDetails
import java.net.URI
import java.net.URL

class OwnPageClientDetails(
    private val clientId: String,
    private val callbackBaseUrl: URL,
    private val scopes: Set<String>,
    private val secret: String
) : ClientDetails {

    companion object {
        const val ACCESS_TOKEN_VALIDITY_SECONDS = 60
        const val GUEST_ACCESS_REDIRECT_PREFIX_PATH = "/api/login/oauth2/code"
        private enum class GrantTypes(val value: String) {
            AUTHORIZATION_CODE("authorization_code")
        }
    }

    override fun isSecretRequired(): Boolean = false
    override fun getAdditionalInformation(): Map<String, Any> =
        emptyMap()

    override fun getAccessTokenValiditySeconds(): Int =
        ACCESS_TOKEN_VALIDITY_SECONDS

    override fun getClientId(): String = clientId

    override fun getResourceIds(): Set<String> = emptySet()
    override fun getClientSecret(): String = secret
    override fun getRegisteredRedirectUri(): Set<String> {
        return setOf(
            URI(
                callbackBaseUrl.protocol,
                null,
                callbackBaseUrl.host,
                callbackBaseUrl.port,
                "$GUEST_ACCESS_REDIRECT_PREFIX_PATH/$clientId",
                null,
                null
            ).toURL().toString()
        )
    }

    override fun isScoped(): Boolean = true
    override fun getScope(): Set<String> = scopes
    override fun getAuthorizedGrantTypes() = setOf(GrantTypes.AUTHORIZATION_CODE.value)

    override fun isAutoApprove(scope: String?): Boolean = false
    override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()

    override fun getRefreshTokenValiditySeconds(): Int = 0
}
