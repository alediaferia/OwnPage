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

import com.alediaferia.ownpage.config.oauth2.login.GuestOAuth2User
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.oauth2.jwt.JwtClaimNames
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter

// TODO: e2e test the content of the authentication
class OwnUserAuthenticationConverter(private val userDetailsService: UserDetailsService) : UserAuthenticationConverter {
    var usernameKey: String = DEFAULT_USERNAME_KEY

    override fun extractAuthentication(map: MutableMap<String, *>): Authentication? {
        val name = map[usernameKey] as? String
        return name?.let {
            val user: UserDetails = userDetailsService.loadUserByUsername(it)
            UsernamePasswordAuthenticationToken(user, "N/A", user.authorities)
        }
    }

    override fun convertUserAuthentication(userAuthentication: Authentication): MutableMap<String, *> {
        val principal = userAuthentication.principal
        return if (principal is GuestOAuth2User)
            mutableMapOf(
                usernameKey to userAuthentication.name,
                CLIENT_REG_ID_KEY to principal.clientRegistrationId,
                USER_TYPE_KEY to "guest"
            )
        else
            mutableMapOf(
                usernameKey to userAuthentication.name
            )
    }
}

const val DEFAULT_USERNAME_KEY = JwtClaimNames.SUB
const val CLIENT_REG_ID_KEY = "https://alediaferia.com/jwt/claims/client_reg_id"
const val USER_TYPE_KEY = "https://alediaferia.com/jwt/claims/user_type"
