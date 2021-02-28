import { OAuth2 } from './constants';

export const oauthAuthorizeUrl = '/api/oauth/authorize?response_type=code'
export const login = `${oauthAuthorizeUrl}&client_id=${OAuth2.clientId}&scope=profile_manager`;
export const getLoginRoute = (redirectUri: string) => `${oauthAuthorizeUrl}&client_id=${OAuth2.clientId}&scope=profile_manager&redirect_uri=${redirectUri}`

export const oauthTokenUrl = `/api/oauth/token`

export const postCreateUrl = '/api/posts'
export const getPostGetRoute = (postId: string) => `/api/posts/${postId}`
export const getCommentsRoute = (postId: string) => `/api/posts/${postId}/comments`
export const oauthClientValidateUrl = '/api/oauth/guest/validate'
export const userInfoUrl = '/api/users/self/info'