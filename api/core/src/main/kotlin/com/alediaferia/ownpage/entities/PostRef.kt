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
 * Stores a reference to a post on another ownpage. It has an association
 * to the comments on the post authored by the owner of this ownpage. The comments
 * are stored on this ownpage and referenced by the recipient ownpage.
 */
@Entity
@Table(
    name = "post_refs",
    indexes = [
        Index(columnList = "own_page_ref_id,remote_id", unique = true)
    ]
)
class PostRef(
//    @OneToMany(mappedBy = "postRef", targetEntity = Comment::class)
//    var comments: Collection<Comment> = emptyList(),

    @ManyToOne
    @JoinColumn(name = "own_page_ref_id", nullable = false)
    var ownPageRef: OwnPageRef,

    @Column(name = "remote_id", nullable = false, updatable = false)
    var remoteId: UUID,

    @GeneratedValue @Id var id: UUID? = null
)
