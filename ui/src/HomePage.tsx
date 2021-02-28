import React from 'react';
import { Typography } from '@material-ui/core';
import { createStyles, makeStyles, Theme, useTheme } from '@material-ui/core/styles';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {

    }
  })
);

export default function HomePage() {
  const classes = useStyles(useTheme());
  return (
    <div className={classes.root}>
      <Typography>Welcome to OwnPage</Typography>
    </div>
  );
}