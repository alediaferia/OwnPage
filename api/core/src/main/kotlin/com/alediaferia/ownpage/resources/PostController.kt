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

import com.alediaferia.ownpage.entities.Post
import com.alediaferia.ownpage.entities.Profile
import com.alediaferia.ownpage.entities.User
import com.alediaferia.ownpage.models.PostModel
import com.alediaferia.ownpage.models.PostsModel
import com.alediaferia.ownpage.models.toModel
import com.alediaferia.ownpage.repositories.ProfileRepository
import com.alediaferia.ownpage.repositories.UserRepository
import com.alediaferia.ownpage.resources.auth.AuthContextMethods
import com.alediaferia.ownpage.resources.exceptions.ManagedProfileNotFoundException
import com.alediaferia.ownpage.resources.exceptions.ProfileMissingException
import com.alediaferia.ownpage.services.PostService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.util.*

@RestController
@RequestMapping("/posts", produces = [MediaType.APPLICATION_JSON_VALUE])
class PostController(
        private val postService: PostService,
        private val profileRepository: ProfileRepository,
        override val userRepository: UserRepository
) : AuthContextMethods {
    @GetMapping
    fun all(): PostsModel {
        val profile = profileRepository.findFirstByOrderByCreatedAtAsc() ?: throw ProfileMissingException()
        return PostsModel(postService.findAllByProfileId(profile.id!!).map(Post::toModel))
    }

    @GetMapping("/{id}")
    fun get(@PathVariable("id") postId: String): PostModel {
        return postService.getById(UUID.fromString(postId)).toModel()
    }

    @PostMapping
    fun create(@RequestBody post: PostModel, principal: Principal): PostModel {
        val currentUser = principal.currentUser()
        return postService.save(post.toEntity(currentUser.managedProfile())).toModel()
    }

    private fun User.managedProfile(): Profile {
        return managedProfile ?: throw ManagedProfileNotFoundException()
    }
}
