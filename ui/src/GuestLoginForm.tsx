import React, { useState, useEffect } from 'react';
import { createStyles, makeStyles, Theme, useTheme } from '@material-ui/core/styles';
import { Paper, TextField, Button } from '@material-ui/core';
import { Alert } from '@material-ui/lab';
import GuestAuthenticationService from './services/GuestAuthenticationService';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {

    }
  })
);

export default function GuestLoginForm() {
  const [identityOwnerOwnPageUrl, setIdentityOwnerOwnPageUrl] = useState("")
  const [authorizeUrl, setAuthorizeUrl] = useState("")
  const [isValidTargetUrl, setIsValidTargetUrl] = useState(false)
  const [isValidatingTarget, setIsValidatingTarget] = useState(false)
  const [isTargetDisabled, setIsTargetDisabled] = useState(false)
  
  const checkValidUrl = (url: string): boolean => {
    try {
      new URL(url)
      return true
    } catch (ex) {
      if (ex instanceof TypeError) {
        return false
      } else {
        throw ex
      }
    }
  }

  useEffect(() => {
    setIsValidTargetUrl(checkValidUrl(identityOwnerOwnPageUrl))
    setIsTargetDisabled(false)
  }, [identityOwnerOwnPageUrl, setIsValidTargetUrl])

  const tryAuthenticate = async () => {
    const authenticationService = new GuestAuthenticationService(identityOwnerOwnPageUrl)
    const oauthClientDetails = await authenticationService.validateOauthClient()
    setIsValidatingTarget(false)
    if (oauthClientDetails.disabled) {
      setIsTargetDisabled(true)
    } else {
      setIsTargetDisabled(false)
      setAuthorizeUrl(oauthClientDetails.loginUri)

      window.location.pathname = oauthClientDetails.loginUri
    }
  }

  const classes = useStyles(useTheme())
  return (
    <Paper className={classes.root}>
      
      <p>Insert below the address to your OwnPage in order to authenticate as guest here.</p>
      <div>
        <TextField 
          required
          label="OwnPage URL" 
          type="url"
          helperText="https://myawesome.domain.com"
          value={identityOwnerOwnPageUrl}
          onChange={(e) => { setIdentityOwnerOwnPageUrl(e.target.value) }}
          />
        <DisabledAccessBanner show={isTargetDisabled} />
        <Button 
          disabled={!isValidTargetUrl || isValidatingTarget}
          color="primary"
          fullWidth={true}
          onClick={tryAuthenticate}
          >{ isValidatingTarget ? 'Validating...' : 'Authenticate as Guest' }</Button>
      </div>
    </Paper>
  )
}

function DisabledAccessBanner(props: { show: boolean }) {
  if (!props.show) {
    return null
  }

  return (
    <Alert severity="error">Access through the specified OwnPage URL is disabled</Alert>
  )
}