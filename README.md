# DivolteClickStream_E-Commerce
Divolte collector ile e ticaret sitesinden alınan avro tipinde ki tıklama verileri kafka ya produce edilip spark streaming ile avro formatın da kafka dan  okuma yapılarak gelen data yı stream olarak google cloud da ki elasticsearch servisi ne indexlenmiş ve aşağıda ki görsel gibi dashboardlar oluşturulmuştur.

![kibana1](https://user-images.githubusercontent.com/55887187/110013635-ea705800-7d32-11eb-8cc6-d3c8e785ccca.jpg)

### Collector configuration file 
```avro
divolte {
  mappings {
    my_mapping = {
      schema_file = "/home/harezmi/Desktop/divolte-collector-0.9.0/conf/MyEventRecord.avsc"
      mapping_script_file = "/home/harezmi/Desktop/divolte-collector-0.9.0/conf/mapping.groovy"
      sources = [browser]
      sinks = [kafka]
    }
  }
  global {
    server {
      host = 127.0.0.1
      port = 8290
    },
	 kafka {
      // Enable Kafka flushing
      enabled = true

      // The properties under the producer key in this
      // configuration are used to create a Properties object
      // which is passed to Kafka as is. At the very least,
      // configure the broker list here. For more options
      // that can be passed to a Kafka producer, see this link:
      // https://kafka.apache.org/documentation.html#producerconfigs
      producer = {
        bootstrap.servers = "34.69.255.55:9092"
      }
    }
  }

  sinks {
    // The name of the sink. (It's referred to by the mapping.)
    kafka {
      type = kafka

      // This is the name of the topic that data will be produced on
      topic = divolte
    }
  }
}
```
### Colletor Avro Schema File
```avro
{
  "namespace": "io",
  "type": "record",
  "name": "MyEventRecord",
  "fields": [
    { "name": "timestamp",  "type": "long" },
    { "name": "remoteHost", "type": "string"},
    { "name": "eventType",  "type": ["null", "string"],  "default": null },
    { "name": "location",   "type": ["null", "string"],  "default": null },
    { "name": "localPath",  "type": ["null", "string"],  "default": null },
    { "name": "type",       "type": ["null", "string"],  "default": null },
    { "name": "title",      "type": ["null", "string"],  "default": null },
    { "name": "price",      "type": ["null", "string"],  "default": null }
  ]
}
```
### Collector Mapping File
```avro
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


```
