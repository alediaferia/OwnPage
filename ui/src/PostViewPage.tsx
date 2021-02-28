import React from 'react';
import PostViewComponent from './PostViewComponent';
import CommentsComponent from './CommentsComponent'
import { createStyles, makeStyles, Theme, useTheme } from '@material-ui/core/styles';
import { Grid } from '@material-ui/core';
import {
  useParams
} from "react-router-dom";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      formItem: {
        
      }
    }
  })
);

export default function PostViewPage() {
  const classes = useStyles(useTheme())
  let { postId } = useParams();

  return (
    <div className={classes.root}>
      <Grid
        container
        direction="row"
        justify="center"
        alignItems="center">
          <Grid item lg={5} md={8} xs={10}>
            <PostViewComponent postId={postId!} />
          </Grid>
          <Grid item lg={5} md={8} xs={10}>
            <CommentsComponent postId={postId!} />
          </Grid>
        </Grid>
    </div>
  )
}