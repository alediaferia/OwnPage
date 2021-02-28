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

package com.alediaferia.ownpage.utils.extensions.uris

import java.net.URI
import java.net.URL

class UriBuilder private constructor(
    private val uriString: String
) {
    lateinit var baseUrl: String

    companion object {
        fun uri(uriString: String): UriBuilder {
            return UriBuilder(uriString)
        }
    }

    fun withBaseUrl(baseUrl: String): UriBuilder {
        this.baseUrl = baseUrl
        return this
    }

    fun buildUrl(): URL {
        val baseUrl = URL(baseUrl)
        return URI(
            baseUrl.protocol,
            null,
            baseUrl.host,
            baseUrl.port,
            uriString,
            null,
            null
        ).toURL()
    }
}

fun UriBuilder.asString(): String {
    return buildUrl().toString()
}

fun uri(uri: String) = UriBuilder.uri(uri)
