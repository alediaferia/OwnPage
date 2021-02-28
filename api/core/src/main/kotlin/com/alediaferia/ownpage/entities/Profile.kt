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

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.ZonedDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "profiles")
class Profile(
   @Column(name = "first_name")
   var firstName: String? = null,

   @Column(name = "last_name")
   var lastName: String? = null,

   @Column(name = "display_name")
   var displayName: String,

   @Column(columnDefinition = "TEXT")
   var bio: String?,

   @Id @GeneratedValue
   var id: UUID? = null,

   @OneToMany(mappedBy = "profile", targetEntity = Post::class)
   var posts: Collection<Post> = emptyList(),

   @OneToMany(mappedBy = "managedProfile", targetEntity = User::class)
   var managingUsers: Collection<User> = emptyList(),

   @Column(name = "created_at")
   @CreationTimestamp var createdAt: ZonedDateTime? = null,

   @Column(name = "updated_at")
   @UpdateTimestamp var updatedAt: ZonedDateTime? = null
)
