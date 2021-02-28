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

package com.alediaferia.ownpage.resources

import com.alediaferia.ownpage.config.authorizationserver.CLIENT_REG_ID_KEY
import com.alediaferia.ownpage.config.authorizationserver.USER_TYPE_KEY
import com.alediaferia.ownpage.entities.CommentRef
import com.alediaferia.ownpage.entities.LocalComment
import com.alediaferia.ownpage.models.*
import com.alediaferia.ownpage.repositories.AbstractCommentRepository
import com.alediaferia.ownpage.repositories.CommentRefRepository
import com.alediaferia.ownpage.services.GuestOauthLoginClientService
import com.alediaferia.ownpage.services.PostService
import com.alediaferia.ownpage.services.RemotePostService
import com.alediaferia.ownpage.utils.extensions.uris.asString
import com.alediaferia.ownpage.utils.extensions.uris.uri
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ServerErrorException
import java.util.*

@RestController
@RequestMapping("/posts/{postId}/comments")
class PostCommentController(
    private val abstractCommentRepository: AbstractCommentRepository,
    private val commentRefRepository: CommentRefRepository,
    private val postService: PostService,
    private val oauth2WebClient: WebClient,
    private val guestOauthLoginClientService: GuestOauthLoginClientService,
    private val remotePostService: RemotePostService
) {
    @PostMapping
    fun create(
        @PathVariable("postId") postId: String,
        @RequestBody commentModel: CommentModel,
        authentication: Authentication
    ): CommentModel {
        val post = postService.getById(UUID.fromString(postId))
        val principal = authentication.principal
        if (principal is Jwt && principal.getClaimAsString(USER_TYPE_KEY) == "guest") {
            val clientRegId = principal.getClaimAsString(CLIENT_REG_ID_KEY)
            val guestOauthClient = guestOauthLoginClientService.getByRegistrationId(clientRegId)
            // create post ref
            // create comment to post ref

            val postRefResponseModel = remotePostService.getOrCreatePostRef(principal, UUID.fromString(postId))

            val commentResponseModel = oauth2WebClient
                .post()
                .uri(
                    uri("/api/postrefs/${postRefResponseModel.id}/comments")
                        .withBaseUrl(guestOauthClient.baseUrl)
                        .asString()
                )
                .attributes(clientRegistrationId(clientRegId))
                .body(fromValue(commentModel))
                .retrieve()
                .bodyToMono(CommentModel::class.java)
                .block() ?: throw ServerErrorException("Unable to fetch comments for postRef ${postRefResponseModel.id}", null as Throwable?)

            val commentRef = CommentRef(
                remoteId = UUID.fromString(commentResponseModel.id),
                post = post,
                ownPageRef = guestOauthClient.ownPageRef
            )
            return commentRefRepository.save(commentRef).toModel(commentResponseModel.text!!)
        }

        val comment = commentModel.toLocalComment(post)
        return abstractCommentRepository.save(comment).toModel()
    }

    @GetMapping
    fun getAll(@PathVariable("postId") postId: String): CommentsModel {
        val allComments = abstractCommentRepository.findAllByPostId(UUID.fromString(postId))
        val (localComments, commentRefs) = allComments.partition { it is LocalComment }

        return CommentsModel(
            comments =
            localComments.map { (it as LocalComment).toModel() } +
                    fetchCommentRefs(commentRefs.map { it as CommentRef })
        )
    }

    private fun fetchCommentRefs(refs: Collection<CommentRef>) = refs.map(this::fetchCommentRef)

    private fun fetchCommentRef(ref: CommentRef): CommentModel {
        val webClient = WebClient.builder()
            .baseUrl(ref.ownPageRef.baseUrl)
            .build()
            .get()
            .uri("/api/remotecomments/${ref.remoteId}")
            .retrieve()
            .bodyToMono(CommentModel::class.java)
            .onErrorReturn(ref.asUnavailableComment())

        return webClient.block() as CommentModel
    }

    private fun CommentRef.asUnavailableComment() = CommentModel(
        text = null,
        id = id.toString()
    ).apply {
        error = ResourceErrorModel(
            "This comment is currently unavailable",
            "/api/commentrefs/${this@asUnavailableComment.id}"
        )
    }
}
