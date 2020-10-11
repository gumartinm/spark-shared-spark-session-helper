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

import de.example.spark.testing.job.AwesomeJob
import de.example.spark.testing.service.AwesomeService
import org.apache.spark.sql.SparkSession

object AwesomeApp extends App {
  private val sourcePath = args(0) // "s3a://some/awesome/source/path/"
  private val destinationPath = args(1) // "s3a://some/awesome/destination/path/"

  private implicit val sparkSession: SparkSession = SparkSession
    .builder()
    .appName("awesome-app")
    .enableHiveSupport()
    .getOrCreate()

  private val awesomeService = new AwesomeService

  new AwesomeJob(sourcePath, destinationPath, awesomeService).run()
}
