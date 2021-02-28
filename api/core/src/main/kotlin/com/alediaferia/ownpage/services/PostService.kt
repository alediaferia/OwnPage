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

package com.alediaferia.ownpage.services

import com.alediaferia.ownpage.entities.Post
import com.alediaferia.ownpage.repositories.PostRepository
import com.alediaferia.ownpage.resources.exceptions.PostNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class PostService(
    private val postRepository: PostRepository
) {
    fun findAllByProfileId(profileId: UUID): Collection<Post> {
        return postRepository.findAllByProfileId(profileId)
    }

    fun findById(id: UUID): Post? {
        return postRepository.findByIdOrNull(id)
    }

    fun getById(id: UUID): Post {
        return findById(id) ?: throw PostNotFoundException(id)
    }

    fun save(post: Post): Post {
        return postRepository.save(post)
    }
}
