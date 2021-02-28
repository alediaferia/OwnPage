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

package com.alediaferia.ownpage.client

import com.alediaferia.ownpage.models.UserModel
import com.alediaferia.ownpage.utils.TestRestTemplateMethods
import com.alediaferia.ownpage.utils.random
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import java.net.URI

@TestPropertySource(
    properties = [
        "spring.jpa.hibernate.ddl-auto=create",
        "logging.level.org.springframework.boot.test.web.client=DEBUG",
        "logging.level.org.springframework.web=DEBUG",
        "spring.http.log-request-details=true",
        "owner.setup-password=integration-tests-password",
        "ownapp.oauth2.client_id=dummy-id"
    ],
    locations = ["classpath:application-test.yml"]
)
@ActiveProfiles(profiles = ["test"])
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
abstract class AbstractClientTests : TestRestTemplateMethods {
    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    protected lateinit var restTemplate: TestRestTemplate

    @Value("\${server.servlet.context-path}")
    protected lateinit var servletContextPath: String

    protected fun urlFor(uri: String): URI =
        URI("http://localhost:$port$servletContextPath$uri")

    protected fun getTestUser(): UserModel {
        val testUser = UserModel(
            String.random(),
            String.random()
        )

        val userModelResponse = restTemplate
            .withBasicAuth("owner", "integration-tests-password")
            .postForEntity(urlFor("/users/admin"), testUser, UserModel::class.java)

        assertEquals(HttpStatus.OK, userModelResponse.statusCode)
        return UserModel(
            userModelResponse.body!!.name,
            testUser.password,
            userModelResponse.body!!.id
        )
    }
}
