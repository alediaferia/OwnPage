import { getPostGetRoute } from '../routes'

export interface IPost {
  id: string,
  title: string,
  content: string,
  createdAt: string,
  updatedAt: string
}

export const NullPost: IPost = {
  id: '',
  title: '',
  content: '',
  createdAt: '',
  updatedAt: ''
}

export default class PostService {
  constructor(private accessToken: string) {}

  async get(postId: string): Promise<IPost> {
    return fetch(getPostGetRoute(postId), {
      headers: {
        'Content-Type': 'application/json; charset=utf-8',
        'Authorization': `Bearer ${this.accessToken}`
      }
    }).then((response) => {
      if (response.ok)
        return response.json() as Promise<IPost>
      else
        return Promise.resolve(NullPost)
    })
  }
}