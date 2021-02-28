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

import javax.persistence.*
import javax.persistence.DiscriminatorType.INTEGER
import javax.persistence.InheritanceType.TABLE_PER_CLASS

@Entity
@Inheritance(strategy = TABLE_PER_CLASS)
@DiscriminatorColumn(name = "comment_type", discriminatorType = INTEGER)
@Table(name = "comments")
abstract class AbstractComment(

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = true, updatable = false)
    var post: Post
) : AbstractEntity() {
    companion object {
        const val LOCAL_COMMENT_TYPE = "1"
        const val COMMENT_REFERENCE_TYPE = "2"
    }
}
