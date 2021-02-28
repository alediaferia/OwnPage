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

import com.alediaferia.ownpage.clients.RemotePostRefClient
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class PostRefClientFactory(
    private val oauth2WebClient: WebClient
) {

    fun buildRemotePostRefClient(baseUrl: String,
                                 clientRegistrationId: String): PostRefClient {
        return RemotePostRefClient(baseUrl, clientRegistrationId, oauth2WebClient)
    }
}
