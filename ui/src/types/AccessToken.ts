export default interface AccessToken {
  'access_token': string,
  'token_type': string,
  'expires_in': number,
  'refresh_token': string | null,
  'scope': string
};