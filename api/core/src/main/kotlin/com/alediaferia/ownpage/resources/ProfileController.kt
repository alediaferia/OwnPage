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

package com.alediaferia.ownpage.resources

import com.alediaferia.ownpage.models.ProfileModel
import com.alediaferia.ownpage.models.toModel
import com.alediaferia.ownpage.repositories.UserRepository
import com.alediaferia.ownpage.resources.auth.AuthContextMethods
import com.alediaferia.ownpage.services.ProfileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/profile")
class ProfileController(
    private val profileService: ProfileService,
    override val userRepository: UserRepository
) : AuthContextMethods {
    @PostMapping("/register")
    fun register(@RequestBody profile: ProfileModel, principal: Principal): ResponseEntity<*> {
        return ResponseEntity.ok(profileService.create(profile.toEntity(), principal.currentUser()).toModel())
    }
}
