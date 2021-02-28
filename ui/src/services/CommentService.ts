import { getCommentsRoute } from '../routes'
import {IComment, IComments} from '../types/Comment'

export default class CommentService {
  constructor(private accessToken: string) {}

  async getAll(postId: string): Promise<IComment[]> {
    return fetch(getCommentsRoute(postId), {
      headers: {
        'Content-Type': 'application/json; charset=utf-8',
        'Authorization': `Bearer ${this.accessToken}`
      }
    }).then((response) => {
      if (response.ok)
        return (response.json() as Promise<IComments>).then((comments) => comments.comments)
      else
        return Promise.resolve([])
    })
  }

  async post(postId: string, comment: IComment): Promise<IComment> {
    return fetch(getCommentsRoute(postId), {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json; charset=utf-8',
        'Authorization': `Bearer ${this.accessToken}`
      },
      body: JSON.stringify(comment)
    }).then((response) => {
      if (response.ok)
        return response.json() as Promise<IComment>
      else
        return Promise.resolve({id: '', text: 'Comment error'} as IComment)
    })
  }
}