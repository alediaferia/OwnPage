import React from 'react'
import { AuthProvider } from './auth-context'
import { ThemeProvider } from '@material-ui/core/styles'
import { CookiesProvider } from 'react-cookie'
import theme from '../theme'

interface IAppProvidersProps {
  children: any
}

export function AppProviders(props: IAppProvidersProps) {
  return <CookiesProvider>
    <AuthProvider>
      <ThemeProvider theme={theme}>
        {props.children}
      </ThemeProvider>
    </AuthProvider>
  </CookiesProvider>
}