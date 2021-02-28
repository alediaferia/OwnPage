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

package com.alediaferia.ownpage.config

import com.alediaferia.ownpage.auth.jwt.JwtCustomHeadersAccessTokenConverter
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.RSAKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import java.security.KeyFactory
import java.security.KeyPair
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec


// TODO: Implement asymmetric JWT token encryption
@Configuration
class JwtTokenConfiguration(
) {
    companion object {
        private const val KEY_ID_ATTRIBUTE_NAME = "kid"
        private const val KEY_ID = "ownpage-key-id"
    }
    @Bean
    fun jwtAccessTokenConverter(keyPair: KeyPair): JwtAccessTokenConverter {
        return JwtCustomHeadersAccessTokenConverter(
                mapOf(KEY_ID_ATTRIBUTE_NAME to KEY_ID),
                keyPair
        )
    }

    @Bean
    fun tokenStore(accessTokenConverter: JwtAccessTokenConverter): TokenStore {
        return JwtTokenStore(accessTokenConverter)
    }

    @Bean
    fun keyPair(
            @Value("classpath:ownpage-privatekey.der.pkcs8") privateKey: Resource,
            @Value("classpath:ownpage-publickey.der") publicKey: Resource
    ): KeyPair {
        // https://stackoverflow.com/questions/11410770/load-rsa-public-key-from-file
        val privateKeyBytes = privateKey.inputStream.readAllBytes()
        val privateKeySpec = PKCS8EncodedKeySpec(privateKeyBytes)
        val rsaKeyFactory = KeyFactory.getInstance("RSA")
        val privateKeyInstance = rsaKeyFactory.generatePrivate(privateKeySpec)

        val publicKeyBytes = publicKey.inputStream.readAllBytes()
        val publicKeySpec = X509EncodedKeySpec(publicKeyBytes)
        val publicKeyInstance = rsaKeyFactory.generatePublic(publicKeySpec)

        return KeyPair(publicKeyInstance, privateKeyInstance)
    }


    @Bean
    fun jwkSet(keyPair: KeyPair): JWKSet {
        val builder = RSAKey.Builder(keyPair.public as RSAPublicKey)
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID(KEY_ID)

        return JWKSet(builder.build())
    }
}
