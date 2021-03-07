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

import com.alediaferia.ownpage.OwnpageApplication
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class DualInstancesIntegrationTest {

    companion object {
        lateinit var ownPageApp: ConfigurableApplicationContext
        lateinit var otherPageApp: ConfigurableApplicationContext

        @Container
        val ownPgContainer = PostgreSQLContainer<Nothing>("postgres:12").apply {
            withDatabaseName("owntestdb")
            withUsername("owntestuser")
            withPassword("ownpassword")
            withExposedPorts(5432)
        }

        @Container
        val otherPgContainer = PostgreSQLContainer<Nothing>("postgres:12").apply {
            withDatabaseName("othertestdb")
            withUsername("othertestuser")
            withPassword("otherpassword")
            withExposedPorts(5432)
        }

        @BeforeAll
        @JvmStatic
        fun setup() {
            ownPageApp = SpringApplicationBuilder(OwnpageApplication::class.java)
                .properties(
                    "server.port=8890",
                    "spring.datasource.url=jdbc:postgresql://localhost:${ownPgContainer.firstMappedPort}/owntestdb",
                    "spring.datasource.username=owntestuser",
                    "spring.datasource.password=ownpassword"
                ).run()

            otherPageApp = SpringApplicationBuilder(OwnpageApplication::class.java)
                .properties(
                    "server.port=8891",
                    "spring.datasource.url=jdbc:postgresql://localhost:${otherPgContainer.firstMappedPort}/othertestdb",
                    "spring.datasource.username=othertestuser",
                    "spring.datasource.password=otherpassword"
                ).run()
        }
    }

    @Test
    fun test() {
        assert(true)
    }
}
