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

package com.alediaferia.ownpage.config.authorizationserver

import com.alediaferia.ownpage.config.authorizationserver.clients.GuestClientDetailsService
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import java.util.*

@SpringBootTest
@TestPropertySource(
        properties = [
            "ownapp.oauth2.client_id=dummy-client-id",
            "owner.setup-password=integration-tests-password"
        ],
        locations = [
            "classpath:application-mock.yml"
        ]
)
@ContextConfiguration(
        classes = [
            BCryptPasswordEncoder::class,
            CompositeClientDetailsService::class,
            GuestClientDetailsService::class
        ]
)
@ActiveProfiles(profiles = ["mock"])
class CompositeClientDetailsServiceTest {

    @Autowired
    private lateinit var compositeClientDetailsService: CompositeClientDetailsService

    @MockBean
    private lateinit var guestClientDetailsService: GuestClientDetailsService

    private val base64Encoder = Base64.getUrlEncoder()

    @Test
    fun `it forwards the client id to the guest client details service if appropriate`() {
        val otherPageUrl = "http://otherpage.alediaferia.com"
        val guestClientId = "guest-$otherPageUrl"
        val encodedGuestClientId = base64Encoder.encodeToString(guestClientId.toByteArray())

        given(guestClientDetailsService.loadClientByClientId(guestClientId, "guest-")).willReturn(mock(ClientDetails::class.java))

        compositeClientDetailsService.loadClientByClientId(encodedGuestClientId)

        verify(guestClientDetailsService).loadClientByClientId(encodedGuestClientId, "guest-")
    }
}
