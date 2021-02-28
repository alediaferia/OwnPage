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

package com.alediaferia.ownpage.models

import com.alediaferia.ownpage.entities.GuestOauthLoginClient
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter

data class GuestOauthClientValidateModel(
    var identityOwnerOwnPageUrl: String
)

data class GuestOauthLoginClientModel(
    var registrationId: String,
    var scope: String,
    var loginUri: String,
    var disabled: Boolean = false
)

fun GuestOauthLoginClient.toModel(contextPath: String): GuestOauthLoginClientModel =
    GuestOauthLoginClientModel(
        registrationId!!,
        scopes.joinToString(SCOPES_SEPARATOR),
        "$contextPath${OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI}/$registrationId",
        disabled
    )

private const val SCOPES_SEPARATOR = ","
