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
import com.alediaferia.ownpage.entities.GuestOauthLoginClient
import com.alediaferia.ownpage.entities.OwnPageRef
import com.alediaferia.ownpage.models.OwnPageRefModel
import com.alediaferia.ownpage.models.PostRefModel
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.any
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.security.oauth2.jwt.Jwt
import java.time.Instant
import java.util.*

class RemotePostServiceTest {
    private val localPostService = Mockito.mock(PostService::class.java)

    private var guestOauthLoginClientService = Mockito.mock(GuestOauthLoginClientService::class.java)

    private var postRefClientFactory = Mockito.mock(PostRefClientFactory::class.java)

    private lateinit var remotePostService: RemotePostService

    private val ownPageRefModel = OwnPageRefModel()

    @BeforeEach
    fun setup() {
        remotePostService = RemotePostService(
            localPostService,
            ownPageRefModel,
            guestOauthLoginClientService,
            postRefClientFactory
        )
    }

    @Test
    fun `uses the remote client to get or create the post ref`() {
        val clientRegId = "some-client-id"
        val principal = Jwt("some-jwt-token-value",
            Instant.now(),
            Instant.now(),
            mapOf("a" to "b"),
            mapOf(CLIENT_REG_ID_KEY to clientRegId))

        val clientBaseUrl = Faker().internet().url()
        val postId = UUID.randomUUID()

        val ownPageRef = OwnPageRef(baseUrl = clientBaseUrl)
        val oauthClient = GuestOauthLoginClient(
            clientRegId,
            "",
            "",
            "",
            userInfoUri = "",
            jwkSetUri = "",
            baseUrl = clientBaseUrl,
            ownPageRef = ownPageRef
        )
        Mockito.`when`(guestOauthLoginClientService.getByRegistrationId(clientRegId)).thenReturn(oauthClient)

        val postRefClient = Mockito.mock(PostRefClient::class.java)
        Mockito.`when`(postRefClientFactory.buildRemotePostRefClient(clientBaseUrl, clientRegId)).thenReturn(postRefClient)

        val result = PostRefModel()
        Mockito.`when`(postRefClient.getOrCreatePostRef(any())).thenReturn(result)

        assertEquals(result, remotePostService.getOrCreatePostRef(principal, postId))
        Mockito.verify(postRefClient).getOrCreatePostRef(any())
    }
}
