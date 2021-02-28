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

import com.alediaferia.ownpage.utils.GuestAuthMethods
import com.github.javafaker.Faker
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.client.resource.UserApprovalRequiredException
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.util.OAuth2Utils
import org.springframework.util.LinkedMultiValueMap
import java.net.URI
import java.net.URL

class GuestOauth2Tests : AbstractClientTests(), GuestAuthMethods {

    @Test
    fun `an ownpage client can authenticate as guest`() {
        val user = getTestUser()
        val otherPageUrl = "https://${Faker().internet().url()}"
        val authCodeDetails = AuthorizationCodeResourceDetails().apply {
            clientId = buildGuestClientId(otherPageUrl)
            clientSecret = ""
            scope = listOf("guest")
            accessTokenUri = urlFor("/oauth/token").toString()
            userAuthorizationUri = urlFor("/oauth/authorize").toString()
        }

        val loginData = LinkedMultiValueMap<String, String>(mapOf(
                "username" to listOf(user.name),
                "password" to listOf(user.password)
        ))
        val loginRequest = RequestEntity.post(urlFor("/perform_login"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(loginData)

        val loginResponse: ResponseEntity<String> = restTemplate.postForEntity(loginRequest.url, loginRequest)
        assertEquals(HttpStatus.FOUND, loginResponse.statusCode)

        val clientContext = DefaultOAuth2ClientContext()
        val accessTokenRequest = clientContext.accessTokenRequest
        val expectedRedirectUri = buildRedirectUri(authCodeDetails.clientId, otherPageUrl)

        accessTokenRequest.cookie = loginResponse.headers.getFirst("Set-Cookie")
        accessTokenRequest["redirect_uri"] = expectedRedirectUri

        val oauth2RestTemplate = OAuth2RestTemplate(authCodeDetails, clientContext)
        oauth2RestTemplate.setAccessTokenProvider(AuthorizationCodeAccessTokenProvider())

        val userRedirectException = assertThrows<UserRedirectRequiredException> {
            oauth2RestTemplate.accessToken
        }

        assertEquals(authCodeDetails.userAuthorizationUri, userRedirectException.redirectUri)

        assertThrows<UserApprovalRequiredException> {
            oauth2RestTemplate.accessToken
        }

        accessTokenRequest.set(OAuth2Utils.USER_OAUTH_APPROVAL, "true")

        val token = oauth2RestTemplate.accessToken
        assertNotNull(token)

        assertEquals(OAuth2AccessToken.BEARER_TYPE.toLowerCase(), token.tokenType.toLowerCase())
        assertEquals(setOf("guest"), token.scope)
    }

    private fun buildRedirectUri(clientId: String, baseUrl: String): String {
        val base = URL(baseUrl)
        return URI(
                base.protocol,
                null,
                base.host,
                base.port,
                "/api/login/oauth2/code/$clientId",
                null,
                null
        ).toURL().toString()
    }
}
