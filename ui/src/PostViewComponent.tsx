import React, { useState, useEffect } from 'react'
import { 
  Typography,
  CircularProgress,
  Paper
} from '@material-ui/core'
import { Clear } from '@material-ui/icons';
import { createStyles, makeStyles, Theme, useTheme } from '@material-ui/core/styles';
import { useAuth } from './contexts/auth-context'
import PostService, { IPost, NullPost } from './services/PostService'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
  })
);

export default function PostViewComponent(props: { postId: string}) {
  const classes = useStyles(useTheme())
  const { accessToken } = useAuth()
  const postService = new PostService(accessToken)
  const [post, setPost] = useState<IPost | null>(null)

  useEffect(() => {
    if (post === null) {
      postService.get(props.postId).then(post => setPost(post))
    }
  }, [post])
  return <>
    {(post === null) ? <FetchingPostView /> : ( post === NullPost ? <NullPostView /> : <PostView post={post!} />)}
  </>
}

function PostView(props: { post: IPost }) {
  const { post } = props
  return <>
    <Typography variant='h1'>{post.title}</Typography>
    <Typography variant='body1'>{post.content}</Typography>
  </>
}

const FetchingPostView = () => <>
  <CircularProgress /><Typography variant='body1'>Fetching post...</Typography>
</>

const NullPostView = () => <>
  <Paper>
    <Clear /><Typography variant='body1'>Could not find the post you are looking for.</Typography>
  </Paper>
</>