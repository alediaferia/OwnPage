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

import com.alediaferia.ownpage.entities.PostRef
import com.alediaferia.ownpage.repositories.PostRefRepository
import com.alediaferia.ownpage.resources.exceptions.PostRefNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.net.URL
import java.util.*

@Service
class PostRefService(
    private val postRefRepository: PostRefRepository
) {
    fun validate(ownPageUrl: URL, postUri: String): PostRef {
        TODO()
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    fun getOrCreatePostRef(postRef: PostRef): PostRef {
        return postRefRepository.findOrCreateByRemoteIdAndOwnPageRefId(postRef.remoteId, postRef.ownPageRef.id!!)
    }

    fun findById(id: UUID): PostRef? {
        return postRefRepository.findByIdOrNull(id)
    }

    fun getById(id: UUID): PostRef {
        return findById(id) ?: throw PostRefNotFoundException(id)
    }
}
