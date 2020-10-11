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
package de.example.spark.testing.app

import com.holdenkarau.spark.testing.DataFrameSuiteBase
import de.example.spark.testing.SharedSparkSessionHelper
import org.apache.spark.SparkConf
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row}

class AwesomeAppEndToEndTest extends SharedSparkSessionHelper with DataFrameSuiteBase {

  // Each set of tests may run with its own Spark configuration in an isolated way.
  override def sparkConf: SparkConf = {
    val conf = super.sparkConf
    conf
  }

  it should "run awesome app with success" in {
    val sourcePath = getClass.getResource("/awesomejob/sourcepath/").toString
    val destinationPath = path + "/awesomejob/destinationpath/"
    val args = Array(sourcePath, destinationPath)

    AwesomeApp.main(args)

    val resultDataFrame = sparkSession.sql("SELECT * FROM testing.example")
    val expectedDataFrame = createExpectedDataFrame
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
