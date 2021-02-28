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

import com.alediaferia.ownpage.auth.OWNER_USERNAME
import com.alediaferia.ownpage.auth.OwnerUserDetails
import com.alediaferia.ownpage.auth.role.ProfileManagerRole
import com.alediaferia.ownpage.entities.User
import com.alediaferia.ownpage.repositories.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.security.core.userdetails.User as SpringSecurityUser

@Service
class OwnUserDetailsService(
    @Value("\${owner.setup-password}") private val ownerPassword: String,
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return if (username == OWNER_USERNAME) {
            OwnerUserDetails(
                true,
                passwordEncoder.encode(ownerPassword)
            )
        } else {
            return userRepository.findByName(username)?.toUserDetails() ?: throw UsernameNotFoundException("Unable to find user for name '$username'")
        }
    }

    private fun User.grantedAuthorities(): Collection<GrantedAuthority> {
        return (if (managedProfile != null) {
            ProfileManagerRole.authorities()
        } else emptyList()) + staticAuthorities.map { auth -> SimpleGrantedAuthority(auth) }
    }

    private fun User.toUserDetails(): UserDetails {
        return SpringSecurityUser.withUsername(name)
            .password(password)
            .authorities(grantedAuthorities())
            .build()
    }
}
