export default interface AuthenticationService {
  getAuthenticationUrl(): string
  getClientId(): string
  getTokenUrl(): string
  getRedirectUri(): string
}