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

import com.alediaferia.ownpage.models.ErrorModel
import com.alediaferia.ownpage.models.ProfileModel
import com.alediaferia.ownpage.models.UserModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
    properties = [
        "owner.setup-password=ownpassword"
    ]
)
class ProfileTests : AbstractClientTests() {
    val sampleProfile = ProfileModel(
        "John",
        "Wick",
        "jwick",
        "I'm no one"
    )

    @Test
    fun `an admin user can create a new profile`() {
        val adminUser = restTemplate
            .withBasicAuth("owner", "ownpassword")
            .postForEntity(urlFor("/users/admin"), UserModel("admin", "password"), UserModel::class.java)
        assertEquals(HttpStatus.OK, adminUser.statusCode)

        val result = restTemplate
            .withBasicAuth("admin", "password")
            .postForEntity(urlFor("/profile/register"), sampleProfile, ProfileModel::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val secondResult = restTemplate
            .withBasicAuth("admin", "password")
            .postForEntity(urlFor("/profile/register"), sampleProfile, ErrorModel::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, secondResult.statusCode)
    }

    @Test
    fun testCreateProfileUnauthenticated() {
        val result = restTemplate.postForEntity(urlFor("/profile/register"), sampleProfile, ProfileModel::class.java)
        assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
    }
}
