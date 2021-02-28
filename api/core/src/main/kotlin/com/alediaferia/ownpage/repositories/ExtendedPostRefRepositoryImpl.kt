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

package com.alediaferia.ownpage.repositories

import com.alediaferia.ownpage.entities.OwnPageRef
import com.alediaferia.ownpage.entities.PostRef
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.TypedQuery

@Repository
class ExtendedPostRefRepositoryImpl(
    private val entityManager: EntityManager
) : ExtendedPostRefRepository {

    private val postRefQuery: TypedQuery<PostRef>
        get() = entityManager.createQuery(FIND_BY_POST_URI_QUERY, PostRef::class.java)

    @Transactional(isolation = Isolation.SERIALIZABLE)
    override fun findOrCreateByRemoteIdAndOwnPageRefId(remoteId: UUID, ownPageRefId: UUID): PostRef {
        return with(postRefQuery) {
            setParameter("remoteId", remoteId)
            setParameter("ownPageRefId", ownPageRefId)
        }.resultList.firstOrNull() ?: createWithRemoteIdAndOwnPageRefId(remoteId, ownPageRefId)
    }

    private fun createWithRemoteIdAndOwnPageRefId(remoteId: UUID, ownPageRefId: UUID): PostRef {
        val ownPageRef = entityManager.find(OwnPageRef::class.java, ownPageRefId)
        return PostRef(remoteId = remoteId, ownPageRef = ownPageRef).apply { entityManager.persist(this) }
    }
}

private const val FIND_BY_POST_URI_QUERY =
    "SELECT p FROM PostRef p WHERE remote_id=:remoteId AND own_page_ref_id=:ownPageRefId"
