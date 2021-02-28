import AuthenticationService from "./AuthenticationService";
import AccessToken from "../types/AccessToken";

export default abstract class AbstractAuthenticationService implements AuthenticationService {
  abstract getAuthenticationUrl(): string
  abstract getClientId(): string
  abstract getTokenUrl(): string
  abstract getRedirectUri(): string

  protected constructor(protected scope: string) { }

  async fetchAccessToken(authCode: string): Promise<AccessToken> {
    const formDataPairs = [
      ['grant_type', 'authorization_code'],
      ['code', authCode],
      ['client_id', this.getClientId()],
      ['client_secret', ''],
      ['scope', this.scope],
      ['redirect_uri', this.getRedirectUri()]
    ]

    // reimplemented from https://developer.mozilla.org/en-US/docs/Learn/Forms/Sending_forms_through_JavaScript
    const urlEncodedFormData = 
      formDataPairs.map((pair) => `${encodeURIComponent(pair[0])}=${encodeURIComponent(pair[1])}`)
      .join('&')
      .replace( /%20/g, '+' )

    const response = await fetch(this.getTokenUrl(), {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8',
        'Accept': 'application/json'
      },
      body: urlEncodedFormData
    })

    return response.json() as Promise<AccessToken>
  }
}