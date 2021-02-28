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

import com.alediaferia.ownpage.auth.role.AdminRole
import com.alediaferia.ownpage.entities.User
import com.alediaferia.ownpage.repositories.UserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
    fun createAdmin(userEntity: User): User {
        return userRepository.save(userEntity.ensureIsAdmin())
    }

    private fun User.ensureIsAdmin(): User {
        return apply {
            staticAuthorities = AdminRole.authorities().map(GrantedAuthority::getAuthority)
        }
    }
}
