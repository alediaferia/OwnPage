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

import com.alediaferia.ownpage.SETUP_PASSWORD
import com.alediaferia.ownpage.models.ProfileModel
import com.alediaferia.ownpage.models.UserModel
import com.alediaferia.ownpage.utils.random
import com.github.javafaker.Faker
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.*
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.net.URI

// https://medium.com/javarevisited/integration-tests-with-spring-boot-testcontainers-liquibase-and-junit-5-13fb1ae70b40
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
    properties = [
        "owner.setup-password=$SETUP_PASSWORD"
    ]
)
@Testcontainers
abstract class AbstractTCIntegrationTest {
    @LocalServerPort
    protected var localServerPort: Int = 0

    @Autowired
    protected lateinit var restTemplate: TestRestTemplate

    @Value("\${server.servlet.context-path}")
    protected lateinit var servletContextPath: String

    protected val faker = Faker()

    companion object {
        @Container
        val pgContainer = PostgreSQLContainer<Nothing>("postgres:12").apply {
            withDatabaseName("owntestdb")
            withUsername("owntestuser")
            withPassword("ownpassword")
            withExposedPorts(5432)
        }

        @DynamicPropertySource
        @JvmStatic
        fun registerPgProperty(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") {
                String.format("jdbc:postgresql://localhost:%d/owntestdb", pgContainer.firstMappedPort)
            }
            registry.add("spring.datasource.username") { "owntestuser" }
            registry.add("spring.datasource.password") { "ownpassword" }
        }
    }

    protected fun urlFor(uri: kotlin.String): URI =
        URI("http://localhost:$localServerPort$servletContextPath$uri")

    protected val testUser: UserModel by lazy {
        val testUser = UserModel(
            String.random(),
            String.random()
        )

        val userModelResponse = restTemplate
            .withBasicAuth("owner", SETUP_PASSWORD)
            .postForEntity(urlFor("/users/admin"), testUser, UserModel::class.java)

        assertEquals(HttpStatus.OK, userModelResponse.statusCode)
        UserModel(
            userModelResponse.body!!.name,
            testUser.password,
            userModelResponse.body!!.id
        )
    }

    protected val testProfile: ProfileModel by lazy {
        val sampleProfile = ProfileModel(
            faker.name().firstName(),
            faker.name().lastName(),
            faker.name().username(),
            faker.lorem().paragraph()
        )

        val result = restTemplate
            .withBasicAuth(testUser.name, testUser.password)
            .postForEntity(urlFor("/profile/register"), sampleProfile, ProfileModel::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        result.body!!
    }
}
