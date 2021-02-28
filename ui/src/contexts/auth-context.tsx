import React, { useCallback, useContext, useMemo, useEffect, useState } from 'react'
import { useCookies } from 'react-cookie';
import { CookieNames } from '../constants';
import AccessToken from '../types/AccessToken'

const AuthContext = React.createContext({
  isLoggedIn: false,
  accessToken: (null as unknown as string),
  logout: () => {},
  setAccessTokenData: (data: AccessToken) => {}
})

interface IAuthProviderProps {
  children: any
}

function AuthProvider(props: IAuthProviderProps) {
  const [cookies, setCookie, removeCookie] = useCookies([CookieNames.accessTokenKey]);
  const logout = useCallback(() => removeCookie(CookieNames.accessTokenKey), [removeCookie])
  const setAccessTokenData = useCallback((data) => setCookie(CookieNames.accessTokenKey, data.access_token, {path: '/'}), [setCookie])

  const authenticated = useMemo(() => {
    const accessToken = cookies[CookieNames.accessTokenKey]
    const isLoggedIn = !!accessToken

    return {accessToken, isLoggedIn, setAccessTokenData}
  }, [cookies, setAccessTokenData])

  return (
    <AuthContext.Provider value={Object.assign({}, authenticated, {logout})} {...props} />
  )
}

function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within a AuthProvider')
  }

  return context
}

export {AuthProvider, useAuth};