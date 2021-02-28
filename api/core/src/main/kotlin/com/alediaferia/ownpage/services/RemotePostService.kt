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

import com.alediaferia.ownpage.config.authorizationserver.CLIENT_REG_ID_KEY
import com.alediaferia.ownpage.models.OwnPageRefModel
import com.alediaferia.ownpage.models.PostRefModel
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import java.util.*

/*
 * This service creates post references
 * on remote OwnPage instances pointing to posts
 * on the current OwnPage instance.
 */
@Service
class RemotePostService(
    private val localPostService: PostService,
    private val ownPageRefModel: OwnPageRefModel,
    private val guestOauthLoginClientService: GuestOauthLoginClientService,
    private val postRefClientFactory: PostRefClientFactory
) {
    fun getOrCreatePostRef(principal: Jwt, postId: UUID): PostRefModel {
        localPostService.getById(postId)

        val clientRegId = principal.getClaimAsString(CLIENT_REG_ID_KEY)
        val guestOauthClient = guestOauthLoginClientService.getByRegistrationId(clientRegId)

        val postRefModel = PostRefModel(
            remoteId = postId.toString(),
            ownPageRef = ownPageRefModel
        )

        val webClient = postRefClientFactory.buildRemotePostRefClient(guestOauthClient.baseUrl, clientRegId)

        return webClient.getOrCreatePostRef(postRefModel)
    }
}
