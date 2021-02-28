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

package com.alediaferia.ownpage.entities.converters

import javax.persistence.AttributeConverter

class SimpleCollectionConverter : AttributeConverter<Collection<String>, String> {
    companion object {
        const val COLLECTION_SEPARATOR = ","
    }

    override fun convertToDatabaseColumn(attribute: Collection<String>?): String? {
        return attribute?.joinToString(COLLECTION_SEPARATOR)
    }

    override fun convertToEntityAttribute(dbData: String?): Collection<String>? {
        return dbData?.split(COLLECTION_SEPARATOR) ?: emptyList()
    }
}
