import React, { useState } from 'react';
import { useAuth } from './contexts/auth-context';

import {
  Grid,
  Paper,
  TextField,
  Button
} from '@material-ui/core';
import { createStyles, makeStyles, Theme, useTheme } from '@material-ui/core/styles';
import { postCreateUrl } from './routes';

import { defaultLocale } from './strings';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      padding: `${theme.spacing(8)}px ${theme.spacing(4)}px`
    }
  })
);

type PostData = {
  title: string,
  content: string
}

function createPost(e: React.FormEvent<HTMLFormElement>, title: string, content: string, accessToken: string): void {
  e.preventDefault();

  fetch(postCreateUrl, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json; charset=utf-8',
      'Accept': 'application/json',
      'Authorization': `Bearer ${accessToken}`
    },
    body: JSON.stringify({
      title,
      content
    } as PostData)
  })
}

export default function PostCreateForm() {
  const classes = useStyles(useTheme())
  const [title, setTitle] = useState("")
  const [content, setContent] = useState("")
  const { accessToken } = useAuth()

  return (
    <Paper className={classes.root}>
      <form onSubmit={(e) => createPost(e, title, content, accessToken)}>
        <div>
          <TextField 
            required
            fullWidth
            label={defaultLocale.form.inputTitle} 
            helperText={defaultLocale.form.inputTitleHelperText}
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            />
        </div>
        <div>
          <TextField
            required
            fullWidth
            multiline
            rows={12}
            label={defaultLocale.form.inputContent}
            helperText={defaultLocale.form.inputContentHelperText}
            value={content}
            onChange={(e) => setContent(e.target.value)}
            />
        </div>
        <Grid
          container
          direction="row"
          justify="flex-end"
          alignItems="center">
            <Grid item xs={12} sm={2} md={1}>
              <Button fullWidth={true} variant="contained" color="primary" type="submit">Publish</Button>
            </Grid>
          </Grid>
        </form>
    </Paper>
  )
}