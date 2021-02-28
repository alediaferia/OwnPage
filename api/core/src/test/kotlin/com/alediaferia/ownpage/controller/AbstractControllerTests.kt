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

package com.alediaferia.ownpage.controller

import com.github.javafaker.Faker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc

@TestPropertySource(
    properties = [
        "ownapp.oauth2.client_id=dummy-client-id",
        "spring.profiles.active=mock",
        "owner.setup-password=integration-tests-password",
        "logging.level.org.springframework.boot.test.web.client=DEBUG",
        "logging.level.org.springframework.web=DEBUG",
        "logging.level.org.springframework.security=DEBUG"
    ],
    locations = [
        "classpath:application-mock.yml"
    ]
)
abstract class AbstractControllerTests {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    protected val faker = Faker()
}
