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

package com.alediaferia.ownpage.utils.extensions.strings

import java.util.*

fun String.base64Decoded(): String? {
    val decoder = Base64.getUrlDecoder()
    return try {
        val bytes = decoder.decode(this)
        String(bytes, Charsets.UTF_8)
    } catch (ex: IllegalArgumentException) {
        null
    }
}

val String.base64Maybe: Boolean
    get() = GUEST_CLIENT_ID_REGEX.matches(this)

private val GUEST_CLIENT_ID_REGEX = Regex("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?\$")
