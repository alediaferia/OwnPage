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
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.TypedQuery

@Repository
class ExtendedOwnPageRefRepositoryImpl(
    private val entityManager: EntityManager
) : ExtendedOwnPageRefRepository {

    private val ownPageRefQuery: TypedQuery<OwnPageRef>
        get() = entityManager.createQuery(FIND_BY_URL_QUERY, OwnPageRef::class.java)

    @Transactional(isolation = Isolation.SERIALIZABLE)
    override fun findOrCreateByBaseUrl(baseUrl: String): OwnPageRef {
        return with(ownPageRefQuery) {
            setParameter("baseUrl", baseUrl)
        }.resultList.firstOrNull() ?: OwnPageRef(baseUrl).apply { entityManager.persist(this) }
    }
}

private const val FIND_BY_URL_QUERY =
    "SELECT op FROM OwnPageRef op WHERE base_url=:baseUrl"
