import { oauthClientValidateUrl } from '../routes';

export default class GuestAuthenticationService {
  constructor(private identityOwnerOwnPageUrl: string) { }

  async validateOauthClient(): Promise<GuestOauthClient> {
    const response = await fetch(oauthClientValidateUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json; charset=utf-8',
        'Accept': 'application/json'
      },
      body: JSON.stringify({
        identityOwnerOwnPageUrl: this.identityOwnerOwnPageUrl
      })
    })

    return response.json() as Promise<GuestOauthClient>
  }
}

export interface GuestOauthClient {
  registrationId: string,
  scope: string,
  loginUri: string,
  disabled: boolean
}