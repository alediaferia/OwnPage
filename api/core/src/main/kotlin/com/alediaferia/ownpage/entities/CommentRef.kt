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

package com.alediaferia.ownpage.entities

import java.util.*
import javax.persistence.*

/**
 * Stores a reference to a comment made by another ownpage owner
 * on a post on this ownpage.
 */
@Entity
@DiscriminatorValue(AbstractComment.COMMENT_REFERENCE_TYPE)
class CommentRef(
    @Column(name = "remote_id")
    val remoteId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "own_page_ref_id", nullable = false, updatable = false)
    val ownPageRef: OwnPageRef,

    post: Post
) : AbstractComment(post)
