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
import com.alediaferia.ownpage.models.PostRefModel
import com.alediaferia.ownpage.models.UserModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails
import java.lang.RuntimeException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LocalCommentTests : AbstractClientTests() {

    val dummyComment1 = CommentModel(
        text = "This is a test comment",
    )

    val dummyComment2 = CommentModel(
        text = "This is another test comment",
    )

    lateinit var user: UserModel
    lateinit var oauth2RestTemplate: OAuth2RestTemplate

    lateinit var post: PostModel

    @BeforeAll
    fun initializeAuthentication() {
        user = getTestUser()

        val resourceOwnerDetails = ResourceOwnerPasswordResourceDetails().apply {
            username = user.name
            password = user.password
            scope = listOf("own")
            clientId = "dummy-id"
            clientSecret = "dummy-secret"
            accessTokenUri = urlFor("/oauth/token").toString()
        }

        oauth2RestTemplate = OAuth2RestTemplate(resourceOwnerDetails, DefaultOAuth2ClientContext(DefaultAccessTokenRequest()))

        post = oauth2RestTemplate.postForObject(urlFor("/posts"),
            PostModel(title = "Dummy title", content = "awesome  content"),
            PostModel::class.java) ?: throw RuntimeException("Unable to create post")
    }


    @Test
    fun testCreateComment() {
        val result = oauth2RestTemplate.postForObject(urlFor("/posts/${post.id}/comments"), dummyComment1, CommentModel::class.java)
        assertEquals(dummyComment1.text, result?.text)
    }
}
