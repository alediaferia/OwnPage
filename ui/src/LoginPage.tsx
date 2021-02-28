import React from 'react';
import { Grid, Typography } from '@material-ui/core';
import { createStyles, makeStyles, Theme, useTheme } from '@material-ui/core/styles';
import LoginForm from './LoginForm';
import GuestLoginForm from './GuestLoginForm';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      padding: `${theme.spacing(8)}px ${theme.spacing(2)}px`
    },
    title: {
      color: 'inherit'
    }
  })
);


export default function LoginPage() {
  const classes = useStyles(useTheme())

  return (
    <div className={classes.root}>
      <Grid
       container
       direction="row"
       justify="center"
       alignItems="center"
       spacing={4}>
         <Grid item xs={8}>
           <Grid
            container
            direction="column"
            justify="center"
            alignItems="center"
            spacing={4}>

              <Grid item xs={6}>
                <LoginForm />
              </Grid>
              <Grid item xs={6}>
                <GuestLoginForm />
              </Grid>
            </Grid>
         </Grid>
       </Grid>
    </div>
  );
}