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

import com.alediaferia.ownpage.entities.Post
import com.alediaferia.ownpage.models.CommentModel
import com.alediaferia.ownpage.repositories.AbstractCommentRepository
import com.alediaferia.ownpage.repositories.ProfileRepository
import com.alediaferia.ownpage.repositories.UserRepository
import com.alediaferia.ownpage.services.PostRefService
import com.alediaferia.ownpage.services.PostService
import com.alediaferia.ownpage.utils.ResourceMethods
import com.alediaferia.ownpage.utils.buildRandomUser
import com.alediaferia.ownpage.utils.randomProfile
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class LocalCommentControllerTests : AbstractControllerTests(),
    ResourceMethods {

    @Autowired
    override lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var userRepository: UserRepository

    @MockBean
    private lateinit var postService: PostService

    @Test
    fun `commenting requires a publish_comment authority`() {
        val randomProfile = faker.randomProfile()
        val randomUser = faker.buildRandomUser().apply {
            managedProfile = randomProfile
        }
        given(userRepository.findByName(randomUser.name)).willReturn(randomUser)

        val post = Post(
            faker.lorem().sentence(),
            faker.lorem().paragraph(),
            id = UUID.randomUUID()
        )

        given(postService.getById(post.id!!)).willReturn(post)
        val commentModel = CommentModel(
            text = faker.lorem().paragraph()
        )

        mockMvc.perform(
            postResource(commentModel, "/posts/${post.id}/comments")
                .with(jwt().jwt { it.subject(randomUser.name) }.authorities(emptyList()))
        ).andExpect(status().isForbidden)
    }
}
