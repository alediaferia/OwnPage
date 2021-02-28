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

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class PasswordAuthenticationManager(
    private val passwordEncoder: PasswordEncoder,
    private val userDetailsService: UserDetailsService
) : AuthenticationManager {
    override fun authenticate(authentication: Authentication): Authentication {
        val userDetails = userDetailsService.loadUserByUsername(authentication.principal as String)
        if (passwordEncoder.matches(authentication.credentials as String, userDetails.password)) {
            return UsernamePasswordAuthenticationToken(authentication.principal,
                authentication.credentials,
                authentication.authorities)
        } else throw BadCredentialsException("User name or password invalid")
    }

}
