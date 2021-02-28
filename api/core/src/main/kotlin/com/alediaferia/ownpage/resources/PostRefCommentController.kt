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

import com.alediaferia.ownpage.entities.RemoteComment
import com.alediaferia.ownpage.models.CommentModel
import com.alediaferia.ownpage.models.CommentsModel
import com.alediaferia.ownpage.models.toModel
import com.alediaferia.ownpage.services.PostRefService
import com.alediaferia.ownpage.services.RemoteCommentService
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/postrefs/{postrefId}/comments")
class PostRefCommentController(
    private val postRefService: PostRefService,
    private val remoteCommentService: RemoteCommentService
) {

    @PostMapping
    fun create(
        @PathVariable("postrefId") postrefId: String,
        @RequestBody commentModel: CommentModel
    ): CommentModel {
        val postRef = postRefService.getById(UUID.fromString(postrefId))
        val remoteComment = commentModel.toRemoteComment(postRef)

        return remoteCommentService.create(remoteComment).toModel()
    }

    @GetMapping
    fun getAll(@PathVariable("postrefId") postrefId: String): CommentsModel {
        return CommentsModel(
            comments = remoteCommentService
                .findAllByPostRefId(UUID.fromString(postrefId))
                .map(RemoteComment::toModel)
        )
    }
}
