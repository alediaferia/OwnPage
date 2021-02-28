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

package com.alediaferia.ownpage.services

import com.alediaferia.ownpage.entities.Profile
import com.alediaferia.ownpage.entities.User
import com.alediaferia.ownpage.repositories.ProfileRepository
import com.alediaferia.ownpage.repositories.UserRepository
import com.alediaferia.ownpage.resources.exceptions.ProfileAlreadyExistingException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfileService(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository
) {
    @Transactional(readOnly = false)
    fun create(profile: Profile, owner: User): Profile {
        if (profileRepository.count() > 0)
            throw ProfileAlreadyExistingException()

        return profileRepository.save(profile).also {
            owner.managedProfile = it
            userRepository.save(owner)
        }
    }
}
