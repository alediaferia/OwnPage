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

package com.alediaferia.ownpage.client

import com.alediaferia.ownpage.auth.CreateProfileAuthority
import com.alediaferia.ownpage.models.UserModel
import com.alediaferia.ownpage.repositories.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.TestPropertySource
import java.util.*

@TestPropertySource(
    properties = [
        "owner.setup-password=ownpassword"
    ]
)
class UserTests : AbstractClientTests() {
    @Autowired lateinit var passwordEncoder: PasswordEncoder
    @Autowired lateinit var userRepository: UserRepository

    val sampleUser = UserModel(
        "myuser",
        "mypassword"
    )

    val ownerUser = UserModel(
        "owner",
        "mypassword"
    )

    @Test
    fun testCreateAdmin() {
        val userModel = restTemplate
            .withBasicAuth("owner", "ownpassword")
            .postForObject(urlFor("/users/admin"), sampleUser, UserModel::class.java)

        assertEquals(sampleUser.name, userModel.name)
        val persistedUser = userRepository.findById(UUID.fromString(userModel.id)).get()
        assertTrue(passwordEncoder.matches(sampleUser.password, persistedUser.password))
        assertTrue(persistedUser.staticAuthorities.contains(CreateProfileAuthority.authority))
    }

    @Test
    fun testCreateAdminWithOwnerNameFails() {
        val result = restTemplate
            .withBasicAuth("owner", "ownpassword")
            .postForEntity(urlFor("/users/admin"), ownerUser, UserModel::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }
}
