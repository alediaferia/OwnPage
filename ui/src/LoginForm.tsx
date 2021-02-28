import React, { useState } from 'react';
import { Paper, Grid, TextField, Button } from '@material-ui/core';
import { createStyles, makeStyles, Theme, useTheme } from '@material-ui/core/styles';
import { Face, Fingerprint } from '@material-ui/icons';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      padding: theme.spacing(2, 4)
    },
    title: {
      color: 'inherit'
    },
    submitButton: {
      margin: theme.spacing(2, 4),
    }
  })
);

function LoginForm() {
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const classes = useStyles(useTheme())

  return (
    <Paper className={classes.root}>
      <form action="/api/perform_login" method="post">
        <input type="hidden" name="username" value={username} />
        <input type="hidden" name="password" value={password} />
        <Grid container spacing={8} alignItems="flex-end">
          <Grid item>
            <Face />
          </Grid>
          <Grid item md={true} sm={true} xs={true}>
            <TextField
              id="username"
              label="Username"
              type="text" fullWidth autoFocus required onChange={(e) => setUsername(e.target.value)} />
          </Grid>
        </Grid>
        <Grid container spacing={8} alignItems="flex-end">
          <Grid item>
            <Fingerprint />
          </Grid>
          <Grid item md={true} sm={true} xs={true}>
            <TextField
              id="password"
              label="Password"
              type="password"
              fullWidth required onChange={(e) => setPassword(e.target.value)} />
          </Grid>
        </Grid>
        <Grid container justify="center" style={{ marginTop: '10px' }}>
          <Button className={classes.submitButton} type="submit" color="primary" fullWidth={true}>Login</Button>
        </Grid>
      </form>
    </Paper>
  );
}

export default LoginForm;