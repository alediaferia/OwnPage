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

import com.alediaferia.ownpage.entities.OwnPageRef
import com.alediaferia.ownpage.entities.PostRef
import com.alediaferia.ownpage.models.OwnPageRefModel
import com.alediaferia.ownpage.models.PostRefModel
import com.alediaferia.ownpage.models.toModel
import com.alediaferia.ownpage.resources.exceptions.MissingExpectedFieldException
import com.alediaferia.ownpage.services.GuestOauthLoginClientService
import com.alediaferia.ownpage.services.OwnPageRefService
import com.alediaferia.ownpage.services.PostRefService
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@RestController
@RequestMapping("/postrefs")
class PostRefController(
    private val oauth2WebClient: WebClient,
    private val ownPageRefService: OwnPageRefService,
    private val postRefService: PostRefService,
    private val guestOauthLoginClientService: GuestOauthLoginClientService
) {
    @PostMapping
    fun create(@RequestBody postRefModel: PostRefModel, authentication: Authentication): PostRefModel {
        val ownPageRefModel = postRefModel.ownPageRef ?: throw MissingExpectedFieldException("ownPageRef")
        // TODO use a dedicated exception for multiple fields missing
        if (postRefModel.id == null && postRefModel.remoteId == null)
            throw MissingExpectedFieldException("id")
        val ownPageRef = getOrCreateOwnPageRef(ownPageRefModel)
        return getOrCreatePostRef(postRefModel, ownPageRef).toModel()
    }

    @GetMapping("{postRefId}")
    fun get(@PathVariable("postRefId") postRefId: String): PostRefModel {
        return postRefService.getById(UUID.fromString(postRefId)).toModel()
    }

    private fun getOrCreateOwnPageRef(ownPageRefModel: OwnPageRefModel): OwnPageRef {
        val id = ownPageRefModel.id
        val baseUrl = ownPageRefModel.baseUrl
        return when {
            id != null -> {
                ownPageRefService.getById(UUID.fromString(id))
            }
            baseUrl != null -> {
                ownPageRefService.getOrCreateByBaseUrl(baseUrl)
            }
            else -> throw MissingExpectedFieldException("ownPageRef")
        }
    }

    private fun getOrCreatePostRef(postRefModel: PostRefModel, ownPageRef: OwnPageRef): PostRef {
        val remoteId = postRefModel.remoteId ?: throw MissingExpectedFieldException("remoteId")
        return postRefService.getOrCreatePostRef(PostRef(remoteId = UUID.fromString(remoteId), ownPageRef = ownPageRef))
    }
}
