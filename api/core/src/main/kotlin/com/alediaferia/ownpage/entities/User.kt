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

import com.alediaferia.ownpage.entities.converters.SimpleCollectionConverter
import java.util.*
import javax.persistence.*

@Entity
@Table(
    name = "users",
    uniqueConstraints = [UniqueConstraint(columnNames = ["name"])]
)
class User(
    @Column(name = "name")
    var name: String,

    @Column(name = "password")
    var password: String,
    @Id @GeneratedValue var id: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = true)
    var managedProfile: Profile? = null,

    @Column(name = "static_authorities")
    @Convert(converter = SimpleCollectionConverter::class)
    var staticAuthorities: List<String> = emptyList()
)
