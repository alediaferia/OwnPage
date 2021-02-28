import React, { useState, useEffect } from 'react';
import { createStyles, makeStyles, Theme, useTheme } from '@material-ui/core/styles';
import {
  Button,
  Typography,
  Link,
  Menu,
  MenuItem
} from '@material-ui/core';
import { login as loginRoute, userInfoUrl, getLoginRoute } from '../routes';
import { toLocalUrl } from '../utils';
import { useAuth } from '../contexts/auth-context';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {

    },
    title: {
      color: 'inherit'
    },
    button: {
      color: 'inherit'
    }
  })
);

function LoginStatusWidget() {
  const { isLoggedIn, accessToken, logout } = useAuth();

  const classes = useStyles(useTheme());
  const cta = (isLoggedIn ?
    <LoggedInMenuComponent accessToken={accessToken!} logout={logout} /> :
    <LogInMenuComponent />
  )
  return <div className={classes.root}>{cta}</div>
}

interface UserInfo {
  name: string
}

function LoggedInMenuComponent(props: {accessToken: string, logout: () => void}) {
  const [userName, setUserName] = useState("Logged in")
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

  useEffect(() => {
    fetch(userInfoUrl, {
      headers: {
        'Authorization': `Bearer ${props.accessToken}`
      }
    })
      .then((response) => response.json() as Promise<UserInfo>)
      .then((info) => setUserName(info.name))
      .catch(() => props.logout())
  }, [setUserName])

  const handleClose = () => setAnchorEl(null)
  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const classes = useStyles(useTheme());

  return <div>
      <Button className={classes.button} onClick={handleClick}>
        {userName}
      </Button>
      <Menu
        id="loggedin-menu"
        anchorEl={anchorEl}
        keepMounted
        open={Boolean(anchorEl)}
        onClose={handleClose}
      >
        <MenuItem onClick={handleClose}>Profile</MenuItem>
        <MenuItem onClick={() => { props.logout(); handleClose() }}>Logout</MenuItem>
      </Menu>
    </div>
}

function LogInMenuComponent() {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const handleClose = () => setAnchorEl(null)
  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const classes = useStyles(useTheme());

  return <div>
      <Button className={classes.button} onClick={handleClick}>
        Log in
      </Button>
      <Menu
        id="loggedin-menu"
        anchorEl={anchorEl}
        keepMounted
        open={Boolean(anchorEl)}
        onClose={handleClose}
      >
        <MenuItem onClick={handleClose} component={Link} href={getLoginRoute(toLocalUrl('/authenticated'))}>Log in</MenuItem>
        <MenuItem onClick={handleClose}>Guest log in</MenuItem>
      </Menu>
    </div>
}

export default LoginStatusWidget;