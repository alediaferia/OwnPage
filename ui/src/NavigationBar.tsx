import React from 'react';
import { createStyles, makeStyles, Theme, useTheme } from '@material-ui/core/styles';

import {
    AppBar,
    Toolbar,
    Grid
} from '@material-ui/core';

import LoginStatusWidget from './widgets/LoginStatusWidget';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
    },
    menuButton: {
      marginRight: theme.spacing(2),
    },
    title: {
      flexGrow: 1,
    },
  }),
);

export default function NavigationBar() {
  const classes = useStyles(useTheme());

  return (
    <div className={classes.root}>
      <AppBar position="static">
        <Toolbar>
          <Grid
            container
            direction="row"
            justify="flex-end"
            alignItems="center">
              <Grid item>
                <LoginStatusWidget />
              </Grid>
            </Grid>
        </Toolbar>
      </AppBar>
    </div>
  );
}
