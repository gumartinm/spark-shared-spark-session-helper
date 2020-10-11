// Author: Gustavo Martin Morcuende

/**
 * Copyright 2020 Gustavo Martin Morcuende
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.example.spark.testing.job

import com.holdenkarau.spark.testing.DataFrameSuiteBase
import de.example.spark.testing.SharedSparkSessionHelper
import de.example.spark.testing.service.AwesomeService
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.mockito.MockitoSugar
import org.mockito.scalatest.ResetMocksAfterEachTest

class AwesomeJobIntegrationTest
    extends SharedSparkSessionHelper
    with MockitoSugar
    with ResetMocksAfterEachTest
    with DataFrameSuiteBase {

  // Each set of tests may run with its own Spark configuration in an isolated way.
  override def sparkConf: SparkConf = {
    val conf = super.sparkConf
    conf.set("spark.sql.sources.partitionOverwriteMode", "dynamic")
  }

  it should "run awesome job with success" in {
    val sourcePath = getClass.getResource("/awesomejob/sourcepath//").toString
    val destinationPath = path + "/awesomejob/destinationpath/"
    val awesomeService = mock[AwesomeService]
    val schema = StructType(
      List(
        StructField("name", StringType),
        StructField("surname", StringType)
      )
    )
    val expectedSchema = StructType(
      List(
        StructField("NAME", StringType),
        StructField("SURNAME", StringType)
      )
    )
    when(awesomeService.renameColumnsToUpperCase(schema)).thenReturn(expectedSchema)

    new AwesomeJob(sourcePath, destinationPath, awesomeService).run()

    val resultDataFrame = sparkSession.sql("SELECT * FROM testing.example")
    val expectedDataFrame = createExpectedDataFrame

    verify(awesomeService, times(wantedNumberOfInvocations = 1)).renameColumnsToUpperCase(schema)
    assertDataFrameEquals(expectedDataFrame, resultDataFrame)
  }

  private def createExpectedDataFrame: DataFrame =
    sparkSession.createDataFrame(
      sparkContext.parallelize(
        Seq(
          Row("John", "Doe"),
          Row("Jane", "Doe")
        )),
      StructType(
        List(
          StructField("NAME", StringType),
          StructField("SURNAME", StringType)
        )
      )
    )
}
