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

package com.alediaferia.ownpage.entities

import com.alediaferia.ownpage.auth.oauth2.Scopes
import javax.persistence.*
import javax.persistence.FetchType.LAZY

@Entity
@DiscriminatorValue(OauthLoginClient.GUEST_CLIENT_TYPE)
class GuestOauthLoginClient(
        registrationId: String,
        clientId: String,
        authorizationUri: String,
        tokenUri: String,
        scopes: MutableSet<String> = mutableSetOf(Scopes.GUEST_SCOPE),
        redirectUriTemplate: String = DEFAULT_REDIRECT_URI_TEMPLATE,
        userInfoUri: String,
        jwkSetUri: String,
        baseUrl: String,

        @OneToOne(fetch = LAZY)
        @JoinColumn(name = "own_page_ref_id", nullable = false, updatable = false, unique = false)
        val ownPageRef: OwnPageRef
) : OauthLoginClient(
        registrationId = registrationId,
        clientId = clientId,
        clientSecret = "",
        scopes = scopes,
        authorizationUri = authorizationUri,
        tokenUri = tokenUri,
        redirectUriTemplate = redirectUriTemplate,
        userInfoUri = userInfoUri,
        jwkSetUri = jwkSetUri,
        baseUrl = baseUrl
)
