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

package com.alediaferia.ownpage.auth.jwt

import org.springframework.security.jwt.JwtHelper
import org.springframework.security.jwt.crypto.sign.RsaSigner
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.util.JsonParserFactory
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import java.security.KeyPair
import java.security.interfaces.RSAPrivateKey

// Heavily inspired by
// https://github.com/Baeldung/spring-security-oauth/blob/master/oauth-jws-jwk/oauth-authorization-server-jws-jwk/src/main/java/com/baeldung/config/JwtCustomHeadersAccessTokenConverter.java
class JwtCustomHeadersAccessTokenConverter(
    private val customHeaders: Map<String, String>,
    keyPair: KeyPair
) : JwtAccessTokenConverter() {
    private val objectMapper = JsonParserFactory.create()
    private val signer = RsaSigner(keyPair.private as RSAPrivateKey)

    init {
        setKeyPair(keyPair)
        setSigner(RsaSigner(keyPair.private as RSAPrivateKey))
    }

    override fun encode(accessToken: OAuth2AccessToken, authentication: OAuth2Authentication): String {
        val content: String
        content = try {
            objectMapper.formatMap(accessTokenConverter.convertAccessToken(accessToken, authentication))
        } catch (ex: Exception) {
            throw IllegalStateException("Cannot convert access token to JSON", ex)
        }
        return JwtHelper.encode(content, signer, customHeaders)
                .encoded
    }
}
