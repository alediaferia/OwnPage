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

import com.alediaferia.ownpage.entities.OwnPageRef
import com.alediaferia.ownpage.repositories.OwnPageRefRepository
import com.alediaferia.ownpage.resources.exceptions.FailedOwnPageHandshakeException
import com.alediaferia.ownpage.resources.exceptions.InvalidBaseUrlException
import com.alediaferia.ownpage.resources.exceptions.OwnPageRefNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import java.net.MalformedURLException
import java.net.URL
import java.util.*

@Service
class OwnPageRefService(
    private val ownPageRefRepository: OwnPageRefRepository
) {
    @Transactional(readOnly = true)
    fun findById(uuid: UUID): OwnPageRef? {
        return ownPageRefRepository.findByIdOrNull(uuid)
    }

    @Transactional(readOnly = true)
    fun getById(uuid: UUID): OwnPageRef =
        findById(uuid) ?: throw OwnPageRefNotFoundException(uuid)

    @Transactional
    fun getOrCreateByBaseUrl(baseUrl: String): OwnPageRef {
        val url = try {
            URL(baseUrl)
        } catch (ex: MalformedURLException) {
            throw InvalidBaseUrlException(baseUrl)
        }

        return ownPageRefRepository.findOrCreateByBaseUrl(url.toString())
    }

    private fun validateByBaseUrl(baseUrl: URL) {
        val webClient = WebClient.builder()
            .baseUrl(baseUrl.toString())
            .build()

        // now we need to make sure the specified url is reachable
        val response = webClient
            .head()
            .uri("/")
            .retrieve()
            .toBodilessEntity()
            .block() ?: throw FailedOwnPageHandshakeException(baseUrl.toString())

        if (response.statusCode != HttpStatus.OK)
            throw FailedOwnPageHandshakeException(baseUrl.toString())
    }
}
