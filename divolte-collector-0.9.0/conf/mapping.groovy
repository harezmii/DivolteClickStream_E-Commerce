mapping {
  map timestamp() onto 'timestamp'
  map remoteHost() onto 'remoteHost'
  map eventType() onto 'eventType'
  map location() onto 'location'

  def locationUri = parse location() to uri
  def localUri = parse locationUri.rawFragment() to uri
  map localUri.path() onto 'localPath'
  map eventParameter('type') onto 'type'
  map eventParameter('title') onto 'title'
  map eventParameter('price') onto 'price'
}


