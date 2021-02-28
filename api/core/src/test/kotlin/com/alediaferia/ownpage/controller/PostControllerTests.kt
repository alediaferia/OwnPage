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

import com.alediaferia.ownpage.auth.PublishPostAuthority
import com.alediaferia.ownpage.entities.Post
import com.alediaferia.ownpage.models.PostModel
import com.alediaferia.ownpage.repositories.PostRepository
import com.alediaferia.ownpage.repositories.ProfileRepository
import com.alediaferia.ownpage.repositories.UserRepository
import com.alediaferia.ownpage.resources.PostController
import com.alediaferia.ownpage.utils.PostMethods
import com.alediaferia.ownpage.utils.PostModelMethods
import com.alediaferia.ownpage.utils.buildRandomUser
import com.alediaferia.ownpage.utils.randomProfile
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.javafaker.Faker
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

// TODO Fix
@Disabled
@WebMvcTest(PostController::class)
class PostControllerTests :
    PostModelMethods,
    PostMethods {
    @MockBean
    private lateinit var postRepository: PostRepository

    @MockBean
    private lateinit var userRepository: UserRepository

    @MockBean
    private lateinit var profileRepository: ProfileRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val faker = Faker()

    @Test
    fun `posts are publicly accessible`() {
        val profile = faker.randomProfile()
        val posts = generatePostsForProfile(profile, 2)
        given(profileRepository.findFirstByOrderByCreatedAtAsc()).willReturn(profile)
        given(postRepository.findAllByProfileId(profile.id!!)).willReturn(posts)

        mockMvc.perform(get("/posts", profile.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.posts[0].id").value(posts.first().id.toString()))

        verify(postRepository).findAllByProfileId(profile.id!!)
        verify(profileRepository).findFirstByOrderByCreatedAtAsc()
    }

    @Test
    fun `writing a post is restricted for anonymous access`() {
        val post = PostModel("Title", "content")
        mockMvc.perform(post("/posts").content(objectMapper.writeValueAsString(post)))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `creating a post for a profile can only be performed by role managers`() {
        val post = getRandomPostModel()
        val unauthorizedUser = faker.buildRandomUser()

        given(userRepository.findByName(unauthorizedUser.name)).willReturn(unauthorizedUser)

        mockMvc.perform(
            post("/posts")
                .content(objectMapper.writeValueAsString(post))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(unauthorizedUser.name))
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `creating a post associates it with the profile automatically`() {
        val postModel = getRandomPostModel()
        val profile = faker.randomProfile()
        val user = faker.buildRandomUser().apply {
            managedProfile = profile
        }

        val post = postModel.toEntity(profile).apply {
            id = UUID.randomUUID()
        }

        given(userRepository.findByName(user.name)).willReturn(user)
        given(postRepository.save(ArgumentMatchers.any(Post::class.java))).willReturn(post)

        mockMvc.perform(
            post("/posts")
                .content(objectMapper.writeValueAsString(postModel))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().jwt { it.subject(user.name) }.authorities(PublishPostAuthority))
        )
            .andExpect(jsonPath("$.id").value(post.id.toString()))

        verify(postRepository).save(ArgumentMatchers.any(Post::class.java))
    }
}
