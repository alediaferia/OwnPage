import { oauthTokenUrl, oauthAuthorizeUrl } from '../routes';
import { OAuth2 } from '../constants';
import AbstractAuthenticationService from "./AbstractAuthenticationService";

export default class DefaultAuthenticationService extends AbstractAuthenticationService {
  public constructor(scope: string, private redirectUri: string) {
    super(scope)
  }
  
  getAuthenticationUrl(): string {
    return oauthAuthorizeUrl
  }

  getClientId(): string {
    return OAuth2.clientId
  }

  getTokenUrl(): string {
    return oauthTokenUrl
  }

  getRedirectUri(): string {
    return this.redirectUri
  }
}