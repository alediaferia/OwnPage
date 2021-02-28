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

import com.alediaferia.ownpage.entities.converters.SimpleSetConverter
import java.util.*
import javax.persistence.*
import javax.persistence.DiscriminatorType.INTEGER

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "client_type", discriminatorType = INTEGER)
@Table(
    name = "oauth_login_clients",
    indexes = [
        Index(
            columnList = "registration_id",
            unique = true
        ),
        Index(
            columnList = "client_id",
            unique = true
        )
    ]
)
abstract class OauthLoginClient(
    @Column(name = "registration_id", nullable = false, updatable = false)
    var registrationId: String? = null,

    @Column(name = "client_id", nullable = false, updatable = false)
    var clientId: String,

    @Column(name = "client_secret", nullable = false, updatable = false)
    var clientSecret: String,

    @Column(name = "authorization_uri", nullable = false)
    var authorizationUri: String,

    @Column(name = "token_uri", nullable = false)
    var tokenUri: String,

    @Column(name = "scope", nullable = false)
    @Convert(converter = SimpleSetConverter::class)
    val scopes: MutableSet<String> = mutableSetOf(),

    @Column(name = "redirect_uri_template", nullable = false)
    var redirectUriTemplate: String = DEFAULT_REDIRECT_URI_TEMPLATE,

    @Column(name = "user_info_uri", nullable = false)
    var userInfoUri: String,

    @Column(name = "jwk_set_uri", nullable = false)
    var jwkSetUri: String,

    @Column(name = "base_url", nullable = false)
    var baseUrl: String,

    @Column(name = "disabled", nullable = false)
    var disabled: Boolean = false
) {
    companion object {
        const val GUEST_CLIENT_TYPE = "1"
        const val REGISTERED_CLIENT_TYPE = "2"
        const val DEFAULT_REDIRECT_URI_TEMPLATE = "{baseUrl}/{action}/oauth2/code/{registrationId}"
    }

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    var id: UUID? = null
}
