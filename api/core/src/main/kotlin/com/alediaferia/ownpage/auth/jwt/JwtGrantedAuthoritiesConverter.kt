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

package com.alediaferia.ownpage.auth.jwt

import com.alediaferia.ownpage.auth.role.Role
import com.alediaferia.ownpage.auth.role.RoleProvider
import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

class JwtGrantedAuthoritiesConverter(private val roleProvider: RoleProvider) : Converter<Jwt, Collection<GrantedAuthority>> {
    override fun convert(source: Jwt): Collection<GrantedAuthority> {
        val roleIdentifiers: Collection<*> = when (val scope: Any? = source.getClaim(ROLE_CLAIM_NAME)) {
            is String -> listOf(scope)
            is Collection<*> -> scope
            else -> emptyList<String>()
        }

        val roleForScope = roleIdentifiers.mapNotNull {
            if (it is String) {
                roleProvider.getRoleForIdentifier(it)
            } else null
        }
        return roleForScope.flatMap(Role::authorities)
    }
}

private const val ROLE_CLAIM_NAME = "scope"
