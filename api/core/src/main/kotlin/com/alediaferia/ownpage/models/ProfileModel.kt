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

import com.alediaferia.ownpage.entities.Profile

class ProfileModel(
    val firstName: String? = null,
    val lastName: String? = null,
    val displayName: String? = null,
    val bio: String? = null,
    val id: String? = null
) {
    fun toEntity(): Profile {
        return Profile(
           firstName,
            lastName,
            displayName!!,
            bio
        )
    }
}

fun Profile.toModel(): ProfileModel =
    ProfileModel(
        firstName,
        lastName,
        displayName,
        bio,
        id!!.toString()
    )
