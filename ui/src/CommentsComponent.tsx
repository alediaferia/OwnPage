import React, { useState, useEffect } from 'react'
import {
  CircularProgress,
  TextField,
  Button,
  Avatar,
  Divider
} from '@material-ui/core'
import {
  Face
} from '@material-ui/icons'
import { createStyles, makeStyles, Theme, useTheme } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem, { ListItemProps } from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemAvatar from '@material-ui/core/ListItemAvatar'
import ListItemText from '@material-ui/core/ListItemText';
import { useAuth } from './contexts/auth-context'
import CommentService from './services/CommentService'
import {IComment} from './types/Comment'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
  })
);

export default function CommentsComponent(props: { postId: string }) {
  const [comments, setComments] = useState([] as IComment[])
  const [commentsInitialized, setCommentsInitialized] = useState(false)
  const { accessToken } = useAuth()
  const commentService = new CommentService(accessToken)

  useEffect((): any => {
    let subscribed = true
    if (commentsInitialized === false) {
      commentService.getAll(props.postId).then((comments: IComment[]) => {
        if (subscribed === true) {
          setCommentsInitialized(true)
          setComments(comments)
        }
      })
    }
    return () => subscribed = false
  }, [commentsInitialized])

  const postCallback = (text: string) => {
    commentService.post(props.postId, { id: null, text: text }).then(() => setCommentsInitialized(false))
  }

  return <>
    <List>
      <CommentsInputComponent postCallback={postCallback} />
    </List>
    <Divider />
    <List>
      {comments.map((comment) => <CommentViewComponent comment={comment} key={`cmt-${comment.id}`} />)}
    </List>
  </>
}

function CommentViewComponent(props: {comment: IComment}) {
  let text = props.comment.error ? props.comment.error.message : props.comment.text

  return <ListItem>
    <ListItemAvatar>
      <Avatar>
        <Face />
      </Avatar>
    </ListItemAvatar>
    <ListItemText primary={text} />
  </ListItem>
}

interface ICommentsInputComponentProps {
  postCallback: (body: string) => void,
}

function CommentsInputComponent(props: ICommentsInputComponentProps) {
  const [commentBody, setCommentBody] = useState('')
  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setCommentBody(event.currentTarget.value);
  };

  const handleSubmit = () => {
    setCommentBody('')
    props.postCallback(commentBody)
  }

  return <ListItem>
    <TextField id="outlined-basic" label="Outlined" variant="outlined" onChange={handleChange} value={commentBody} />
    <Button onClick={handleSubmit}>Post</Button>
  </ListItem>
}