  export const toLocalUrl = (uri: string) => {
    const protocol = window.location.protocol
    const host = window.location.host
    return `${protocol}//${host}${uri}`
  }
