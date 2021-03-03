package App

import org.apache.avro.SchemaBuilder
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.avro._
import org.apache.spark.sql.catalyst.dsl.expressions.StringToAttributeConversionHelper
import org.apache.spark.sql.functions._

object ReadKafka {
  def main(args: Array[String]): Unit = {
    val sparkSession = SparkSession
      .builder()
      .appName("read kafka avro format")
      .master("local")
      .getOrCreate()

    val readKafkaData = sparkSession
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers","34.123.74.41:9092")
      .option("subscribe","divolte")
      .load()

    val castData = readKafkaData.select(
      from_avro(col("key"), SchemaBuilder.builder().stringType().toString()).as("key"),
      from_avro(col("value"), SchemaBuilder.builder().stringType().toString()).as("value"))

//    castData
//      .select(
//        to_avro(col("key")).as("key"),
//        to_avro(col("value")).as("value"))
//      .writeStream
//      .format("console")
//      .outputMode("append")
//      .start()
//      .awaitTermination()

    castData.select("value")
      .writeStream
      .outputMode("append")
      .format("console")
      .start()
      .awaitTermination()


  }
}