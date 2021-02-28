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

import com.alediaferia.ownpage.entities.RemoteComment
import com.alediaferia.ownpage.repositories.RemoteCommentRepository
import com.alediaferia.ownpage.resources.exceptions.RemoteCommentNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class RemoteCommentService(
    private val remoteCommentRepository: RemoteCommentRepository
) {
    fun create(comment: RemoteComment): RemoteComment {
        // TODO: check for comment already exists
        return remoteCommentRepository.save(comment)
    }

    fun findAllByPostRefId(id: UUID) = remoteCommentRepository.findAllByPostRefId(id)

    fun findById(id: UUID) = remoteCommentRepository.findByIdOrNull(id)
    fun getById(id: UUID) = findById(id) ?: throw RemoteCommentNotFoundException(id)
}
