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

import com.alediaferia.ownpage.models.GuestOauthLoginClientModel
import com.alediaferia.ownpage.models.GuestOauthClientValidateModel
import com.alediaferia.ownpage.models.toModel
import com.alediaferia.ownpage.resources.exceptions.InvalidGuestOauthClientException
import com.alediaferia.ownpage.resources.validation.GuestOauthLoginClientValidator
import com.alediaferia.ownpage.services.GuestOauthLoginClientService
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/oauth/guest")
class GuestOauthLoginClientController(
    private val guestOauthLoginClientService: GuestOauthLoginClientService,
    private val guestOauthLoginClientValidator: GuestOauthLoginClientValidator,
    @Value("\${server.servlet.context-path}")
        private val servletContextPath: String
) {
    @PostMapping("/validate")
    fun validate(@RequestBody model: GuestOauthClientValidateModel): GuestOauthLoginClientModel {
        val guestClientIdInfo = try {
            guestOauthLoginClientValidator.validateGuestAuthServer(model.identityOwnerOwnPageUrl)
        } catch (ex: GuestOauthLoginClientValidator.InvalidGuestClientIdException) {
            throw InvalidGuestOauthClientException(ex.message ?: "")
        }

        val client = guestOauthLoginClientService.createOrGet(guestClientIdInfo.encodedClientId, guestClientIdInfo.guestUrl)
        return client.toModel(servletContextPath)
    }
}
