import IResource from './Resource'

export interface IComment extends IResource {
  id: string | null,
  text: string
}

export interface IComments {
  comments: IComment[]
}