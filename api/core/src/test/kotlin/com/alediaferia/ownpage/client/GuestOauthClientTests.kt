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

import com.alediaferia.ownpage.models.GuestOauthLoginClientModel
import com.alediaferia.ownpage.models.GuestOauthClientValidateModel
import com.alediaferia.ownpage.utils.GuestAuthMethods
import com.github.javafaker.Faker
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class GuestOauthClientTests : AbstractClientTests(), GuestAuthMethods {

    @Test
    fun `guest clients can be validated anonymously`() {
        val ownpageServerUrl = "https://${Faker().internet().url()}"
        val guestOauthClientModel = GuestOauthClientValidateModel(
                identityOwnerOwnPageUrl = ownpageServerUrl
        )

        val validatedClientResponse = restTemplate
                .anonymously()
                .postForEntity(urlFor("/oauth/guest/validate"), guestOauthClientModel,
                        GuestOauthLoginClientModel::class.java)

        assertEquals(HttpStatus.OK, validatedClientResponse.statusCode)
        assert(validatedClientResponse.body?.disabled == false)
        val responseBody = validatedClientResponse.body as GuestOauthLoginClientModel
        assertEquals("guest", responseBody.scope)
        assertTrue(responseBody.registrationId.isNotBlank())
    }

    @Test
    fun `the same guest client is returned for the same client id`() {
        val ownpageServerUrl = "https://${Faker().internet().url()}"
        val guestOauthClientModel = GuestOauthClientValidateModel(
                identityOwnerOwnPageUrl = ownpageServerUrl
        )

        val validatedClientResponse1 = restTemplate
                .anonymously()
                .postForEntity(urlFor("/oauth/guest/validate"), guestOauthClientModel,
                        GuestOauthLoginClientModel::class.java)

        assertEquals(HttpStatus.OK, validatedClientResponse1.statusCode)
        val validatedClientResponse2 = restTemplate
                .anonymously()
                .postForEntity(urlFor("/oauth/guest/validate"), guestOauthClientModel,
                        GuestOauthLoginClientModel::class.java)

        assertEquals(HttpStatus.OK, validatedClientResponse2.statusCode)

        assertNotNull(validatedClientResponse1.body)
        assertNotNull(validatedClientResponse2.body)
        assertEquals(validatedClientResponse1.body?.registrationId, validatedClientResponse2.body?.registrationId)
    }
}
