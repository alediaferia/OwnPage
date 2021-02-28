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

package com.alediaferia.ownpage.models

import com.alediaferia.ownpage.entities.*
import java.util.*

data class CommentModel(
    var text: String? = null,
    var id: String? = null
) : AbstractResourceModel() {
    fun toLocalComment(post: Post): LocalComment {
        return LocalComment(text!!, post)
    }

    fun toRemoteComment(postRef: PostRef): RemoteComment {
        return RemoteComment(text!!, postRef)
    }
}

data class CommentsModel(
    val comments: Collection<CommentModel> = emptyList()
)

fun LocalComment.toModel(): CommentModel {
    return CommentModel(
        text,
        id?.let(UUID::toString)
    )
}

fun CommentRef.toModel(text: String): CommentModel {
    return CommentModel(
        text,
        id?.let(UUID::toString)
    )
}

fun RemoteComment.toModel(): CommentModel {
    return CommentModel(
        text,
        id?.let(UUID::toString)
    )
}
