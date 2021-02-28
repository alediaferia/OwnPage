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

package com.alediaferia.ownpage.clients

import com.alediaferia.ownpage.models.PostRefModel
import com.alediaferia.ownpage.resources.exceptions.PostRefRetrievalException
import com.alediaferia.ownpage.services.PostRefClient
import com.alediaferia.ownpage.utils.extensions.uris.asString
import com.alediaferia.ownpage.utils.extensions.uris.uri
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

class RemotePostRefClient(
    private val baseUrl: String,
    private val clientRegId: String,
    private val oauth2WebClient: WebClient
) : PostRefClient {
    override fun getOrCreatePostRef(postRefModel: PostRefModel): PostRefModel {
        return oauth2WebClient
            .post()
            .uri(
                uri("/api/postrefs")
                    .withBaseUrl(baseUrl)
                    .asString()
            )
            .attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId(clientRegId))
            .body(BodyInserters.fromValue(postRefModel))
            .retrieve()
            .bodyToMono(PostRefModel::class.java)
            .block() ?: throw PostRefRetrievalException(postRefModel.id)
    }
}
