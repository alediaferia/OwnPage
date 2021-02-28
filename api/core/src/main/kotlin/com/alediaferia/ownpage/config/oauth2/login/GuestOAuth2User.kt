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

import com.alediaferia.ownpage.auth.role.GuestRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.user.OAuth2User
import java.net.URL

class GuestOAuth2User(
        name: String,
        attributes: Map<String, Any>
) : OAuth2User {
    companion object {
        const val CLIENT_REGISTRATION_ID_ATTRIBUTE_KEY = "client_registration_id"
        const val BASE_URL_ATTRIBUTE_KEY = "base_url"
    }

    private val _name: String = name
    private val _attributes: Map<String, Any> = attributes

    val clientRegistrationId: String
        get() = _attributes[CLIENT_REGISTRATION_ID_ATTRIBUTE_KEY] as String
    val baseUrl: URL
        get() = _attributes[BASE_URL_ATTRIBUTE_KEY] as URL

    override fun getAuthorities(): Collection<GrantedAuthority> = GuestRole.authorities()

    override fun getName(): String {
        return _name
    }

    override fun getAttributes(): Map<String, Any> {
        return _attributes
    }
}
