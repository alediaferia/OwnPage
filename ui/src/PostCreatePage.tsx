import React from 'react';
import PostCreateForm from './PostCreateForm';
import { createStyles, makeStyles, Theme, useTheme } from '@material-ui/core/styles';
import { Grid } from '@material-ui/core';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      formItem: {
        
      }
    }
  })
);

export default function PostCreatePage() {
  const classes = useStyles(useTheme())

  return (
    <div className={classes.root}>
      <Grid
        container
        direction="row"
        justify="center"
        alignItems="center">
          <Grid item lg={5} md={8} xs={10}>
            <PostCreateForm />
          </Grid>
        </Grid>
    </div>
  )
}