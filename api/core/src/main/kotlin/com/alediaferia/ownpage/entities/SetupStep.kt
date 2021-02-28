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

@Entity
@Table(
    name = "setup_steps",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["sequence"]),
        UniqueConstraint(columnNames = ["final"])
    ],
    indexes = [
        Index(columnList = "sequence")
    ]
)
class SetupStep(
    var sequence: Int = 0,
    var final: Boolean = false,
    var identifier: String,
    @GeneratedValue @Id var id: UUID? = null
)
