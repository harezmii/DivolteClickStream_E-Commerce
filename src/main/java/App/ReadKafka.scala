package App

import org.apache.avro.{SchemaBuilder}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.avro._
import org.apache.spark.sql.functions._

import java.nio.file.{Files, Paths}
object ReadKafka {
  def main(args: Array[String]): Unit = {

    val schema = SchemaBuilder
      .record("MyEventRecord")
      .namespace("io")
      .fields()
      .name("timestamp").`type`().longType().noDefault()
      .name("remoteHost").`type`().stringType().noDefault()
      .name("eventType").`type`().nullable().stringType().stringDefault("null")
      .name("location").`type`().nullable().stringType().stringDefault("null")
      .name("localPath").`type`().nullable().stringType().stringDefault("null")
      .name("type").`type`().nullable().stringType().stringDefault("null")
      .name("title").`type`().nullable().stringType().stringDefault("null")
      .name("price").`type`().nullable().stringType().stringDefault("null")
      .endRecord()

    val jsonSchema = new String(
      Files.readAllBytes(Paths.get("/home/harezmi/Desktop/divolte-collector-0.9.0/conf/MyEventRecord.avsc")))

    val sparkSession = SparkSession
      .builder()
      .appName("read kafka avro format")
      .master("local")
      .getOrCreate()

    val readKafkaData = sparkSession
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers","34.69.255.55:9092")
      .option("subscribe","divolte")
      .load()

    val castData = readKafkaData
      .select(from_avro(col("value"),jsonSchema).as("select"))
      .select("select.*")

    castData
      .writeStream
      .outputMode("append")
      .format("org.elasticsearch.spark.sql")
      .option("es.nodes", "35.184.79.5")
      .option("es.port","9200")
      .option("es.nodes.wan.only","true")
      .option("checkpointLocation","checkpointLocation")
      .start("clickstreamdata/_doc")
      .awaitTermination()

  }
}