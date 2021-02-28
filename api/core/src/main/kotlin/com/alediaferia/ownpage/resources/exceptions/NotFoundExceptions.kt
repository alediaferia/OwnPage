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

package com.alediaferia.ownpage.resources.exceptions

import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.server.ResponseStatusException
import java.util.*

sealed class NotFoundException(id: UUID, resourceName: String):
        ResponseStatusException(NOT_FOUND, "Unable to find $resourceName with id '$id'")

class PostNotFoundException(id: UUID) : NotFoundException(id, "post")
class OwnPageRefNotFoundException(id: UUID) : NotFoundException(id, "own_page_ref")
class PostRefNotFoundException(id: UUID) : NotFoundException(id, "post_ref")
class RemoteCommentNotFoundException(id: UUID): NotFoundException(id,  "remote_comment")

class GuestOauthClientNotFoundException(registrationId: String) :
        ResponseStatusException(NOT_FOUND, "Unable to find guest oauth2 client with registrationId $registrationId")
