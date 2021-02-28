import React, { useState, useEffect } from 'react';
import { createStyles, makeStyles, Theme, useTheme } from '@material-ui/core/styles';
import { 
  useLocation,
  Redirect
} from 'react-router-dom';
import { Typography } from '@material-ui/core';
import DefaultAuthenticationService from './services/DefaultAuthenticationService';
import { toLocalUrl } from './utils';
import { useAuth } from './contexts/auth-context'

function useQuery() {
  return new URLSearchParams(useLocation().search);
}

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
    },
    title: {
      color: 'inherit'
    }
  })
);

export enum AuthenticationType {
  Own,
  Guest
}

interface AuthenticationPageProps {
  authType: AuthenticationType,
  uri: string
};

function extractBaseUrl(referrer: string): string {
  let url = new URL(referrer);
  return `${url.protocol}//${url.host}`
}

export default function AuthenticationPage(props: AuthenticationPageProps) {
  const classes = useStyles(useTheme())
  const query = useQuery();
  const [isAuthenticating, setAuthenticating] = useState(false);

  const { isLoggedIn, setAccessTokenData } = useAuth()

  const authService = (props.authType === AuthenticationType.Guest) ? 
  new DefaultAuthenticationService('guest', toLocalUrl(props.uri)) :
    new DefaultAuthenticationService('profile_manager', toLocalUrl(props.uri))

  useEffect((): any => {
    if (!isAuthenticating) {
      const code = query.get('code');
      if (code && !isLoggedIn) {
        setAuthenticating(true);
        let subscribed = true;
        (async (code: string) => {
          const accessTokenObject = await authService.fetchAccessToken(code)
          
          console.debug('Received access token ');
          console.debug(accessTokenObject);
          if (subscribed)
            setAuthenticating(false);
          setAccessTokenData(accessTokenObject);
        })(code);
        return () => subscribed = false
      }
    }
  }, [query, isLoggedIn, isAuthenticating, authService]);

  return (
    <div className={classes.root}>
      { (isLoggedIn) ?
        <Redirect to="/home" /> :
        <Typography>Authorizing...</Typography> }
    </div>
  );
}