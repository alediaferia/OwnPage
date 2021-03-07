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

package com.alediaferia.ownpage.client

import com.alediaferia.ownpage.models.CommentModel
import com.alediaferia.ownpage.models.PostModel
import com.alediaferia.ownpage.models.UserModel
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.test.web.client.postForObject
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.client.resource.UserApprovalRequiredException
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails
import org.springframework.security.oauth2.common.util.OAuth2Utils
import org.springframework.util.LinkedMultiValueMap

class LocalCommentTests : AbstractTCIntegrationTest() {

    val dummyComment1 = CommentModel(
        text = "This is a test comment",
    )

    val dummyComment2 = CommentModel(
        text = "This is another test comment",
    )

    lateinit var oauth2RestTemplate: OAuth2RestTemplate

    @BeforeEach
    fun initializeAuthentication() {
        val user = testUser

        val loginData = LinkedMultiValueMap<String, String>(mapOf(
            "username" to listOf(user.name),
            "password" to listOf(user.password)
        ))
        val loginRequest = RequestEntity.post(urlFor("/perform_login"))
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(loginData)
        val loginResponse: ResponseEntity<String> = restTemplate.postForEntity(loginRequest.url, loginRequest)

        val resourceOwnerDetails = AuthorizationCodeResourceDetails().apply {
            clientId = "change-me-client-id"
            clientSecret = ""
            scope = listOf("profile_manager")
            accessTokenUri = urlFor("/oauth/token").toString()
            userAuthorizationUri = urlFor("/oauth/authorize").toString()
        }

        val accessTokenRequest = DefaultAccessTokenRequest().apply {
            cookie = loginResponse.headers.getFirst("Set-Cookie")
            set("redirect_uri", "http://localhost:8456/authenticated")
        }

        oauth2RestTemplate = OAuth2RestTemplate(resourceOwnerDetails, DefaultOAuth2ClientContext(accessTokenRequest))
        oauth2RestTemplate.setAccessTokenProvider(AuthorizationCodeAccessTokenProvider())

        val userRedirectException = assertThrows<UserRedirectRequiredException> {
            oauth2RestTemplate.accessToken
        }

        oauth2RestTemplate.oAuth2ClientContext.accessToken = oauth2RestTemplate.accessToken

        accessTokenRequest.set(OAuth2Utils.USER_OAUTH_APPROVAL, "true")

        testProfile
    }

    @Test
    fun testCreateComment() {
        val post = oauth2RestTemplate.postForObject(urlFor("/posts"),
            PostModel(title = "Dummy title", content = "awesome  content"),
            PostModel::class.java) ?: throw RuntimeException("Unable to create post")

        val result = oauth2RestTemplate.postForObject(urlFor("/posts/${post.id}/comments"), dummyComment1, CommentModel::class.java)
        assertEquals(dummyComment1.text, result?.text)
    }
}
