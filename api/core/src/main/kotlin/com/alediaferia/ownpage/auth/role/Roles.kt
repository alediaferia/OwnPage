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

package com.alediaferia.ownpage.auth.role

import com.alediaferia.ownpage.auth.CreateProfileAuthority
import com.alediaferia.ownpage.auth.PublishCommentAuthority
import com.alediaferia.ownpage.auth.PublishPostAuthority
import com.alediaferia.ownpage.auth.oauth2.Scopes
import org.springframework.security.core.GrantedAuthority

object ProfileManagerRole : AbstractStringRole(Scopes.PROFILE_MANAGER_SCOPE) {
    override fun authorities(): Collection<GrantedAuthority> {
        return listOf(
            PublishPostAuthority,
            PublishCommentAuthority
        )
    }
}

object GuestRole : AbstractStringRole(Scopes.GUEST_SCOPE) {
    override fun authorities(): Collection<GrantedAuthority> {
        return listOf(
            PublishCommentAuthority
        )
    }
}

object AdminRole : AbstractStringRole(Scopes.ADMIN_SCOPE) {
    override fun authorities(): Collection<GrantedAuthority> {
        return listOf(
            CreateProfileAuthority
        )
    }
}
