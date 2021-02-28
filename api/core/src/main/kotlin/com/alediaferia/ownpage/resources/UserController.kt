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

import com.alediaferia.ownpage.auth.OWNER_USERNAME
import com.alediaferia.ownpage.models.ErrorModel
import com.alediaferia.ownpage.models.UserInfoModel
import com.alediaferia.ownpage.models.UserModel
import com.alediaferia.ownpage.models.toModel
import com.alediaferia.ownpage.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder
) {

    @PostMapping("/admin")
    fun createAdmin(@RequestBody user: UserModel): ResponseEntity<*> {
        if (user.name == OWNER_USERNAME) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorModel("You can't create a user with name '${user.name}'"))
        }

        return ResponseEntity.ok(userService.createAdmin(user.toEntity(passwordEncoder.encode(user.password))).toModel())
    }

    @GetMapping("/self/info")
    fun userInfo(principal: Principal): UserInfoModel {
        return UserInfoModel(principal.name)
    }
}
