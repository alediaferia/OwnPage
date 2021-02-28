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

package com.alediaferia.ownpage.controller

import com.alediaferia.ownpage.ControllerTestConfiguration
import com.alediaferia.ownpage.auth.CreateProfileAuthority
import com.alediaferia.ownpage.models.ProfileModel
import com.alediaferia.ownpage.repositories.UserRepository
import com.alediaferia.ownpage.utils.buildRandomUser
import com.alediaferia.ownpage.utils.randomProfile
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@Import(ControllerTestConfiguration::class)
class ProfileControllerTests : AbstractControllerTests() {

    @MockBean
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun `the newly created profile gets linked to the admin user`() {
        val password = faker.internet().password()
        val admin = faker.buildRandomUser().apply {
            staticAuthorities = listOf(CreateProfileAuthority.authority)
            this.password = passwordEncoder.encode(password)
        }
        val profile = faker.randomProfile()
        given(userRepository.findByName(admin.name)).willReturn(admin)

        mockMvc.perform(
            post("/profile/register")
                .content(objectMapper.writeValueAsString(profile))
                .contentType(MediaType.APPLICATION_JSON)
                .with(httpBasic(admin.name, password))
        )
            .andExpect(jsonPath("$.id").isString)
            .andDo {
                val responseProfile = objectMapper.readValue<ProfileModel>(it.response.contentAsString)
                verify(userRepository).save(ArgumentMatchers.argThat { user ->
                    user.managedProfile?.id == responseProfile.id?.let { id -> UUID.fromString(id) }
                })
            }
    }
}
