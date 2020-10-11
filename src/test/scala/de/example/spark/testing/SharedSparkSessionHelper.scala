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
package de.example.spark.testing

import java.io.File
import java.nio.file.Files
import java.util.UUID

import org.apache.commons.io.FileUtils
import org.apache.spark.sql.{SQLContext, SQLImplicits, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

import scala.reflect.io.Directory

trait SharedSparkSessionHelper extends AnyFlatSpec with BeforeAndAfterEach with BeforeAndAfterAll {

  private var _spark: SparkSession = _
  private var warehouseDir: String = _
  private var metastoreDbDir: String = _

  protected var path: String = _

  protected implicit def sparkSession: SparkSession = _spark

  protected def sparkContext: SparkContext = _spark.sparkContext

  protected def sparkConf: SparkConf = {
    val randomUUID = UUID.randomUUID.toString

    warehouseDir = s"spark-warehouse${File.separator}$randomUUID"
    val warehousePath = new File(warehouseDir).getAbsolutePath

    metastoreDbDir = s"metastore_db${File.separator}$randomUUID"
    val matastoreDbPath = new File(metastoreDbDir).getAbsolutePath

    new SparkConf()
      .set("spark.unsafe.exceptionOnMemoryLeak", "true")
      .set("spark.ui.enabled", "false")
      .set("hive.stats.jdbc.timeout", "80")
      .set("spark.sql.session.timeZone", "UTC")
      .set("spark.sql.warehouse.dir", warehousePath)
      .set("javax.jdo.option.ConnectionURL", s"jdbc:derby:;databaseName=$matastoreDbPath;create=true")
  }

  protected override def beforeAll(): Unit = {
    _spark = SparkSession
      .builder()
      .master("local[2]")
      .appName("test-sql-context")
      .config(sparkConf)
      .enableHiveSupport()
      .getOrCreate()
  }

  protected override def afterAll(): Unit = {
    cleanUpSparkSession()
  }

  protected override def beforeEach(): Unit = {
    path = Files.createTempDirectory(this.getClass.getName).toString
  }

  protected override def afterEach(): Unit = {
    new Directory(new File(path)).deleteRecursively()
    sparkSession.sharedState.cacheManager.clearCache()
    sparkSession.sessionState.catalog.reset()
  }

  protected object testImplicits extends SQLImplicits {
    // scalastyle:off method.name
    protected override def _sqlContext: SQLContext = _spark.sqlContext
    // scalastyle:on method.name
  }

  private def cleanUpSparkSession(): Unit = {
    _spark.close()
    SparkSession.clearActiveSession()
    SparkSession.clearDefaultSession()

    val warehousePath = new File(warehouseDir).getAbsolutePath
    FileUtils.deleteDirectory(new File(warehousePath))

    val matastoreDbPath = new File(metastoreDbDir).getAbsolutePath
    FileUtils.deleteDirectory(new File(matastoreDbPath))
  }

}
