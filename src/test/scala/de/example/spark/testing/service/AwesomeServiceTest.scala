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
package de.example.spark.testing.service

import org.apache.spark.sql.types.{ArrayType, StringType, StructField, StructType}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AwesomeServiceTest extends AnyFlatSpec with Matchers  {

  it should "rename columns to upper case" in {
    val awesomeService = new AwesomeService
    val someSchema = StructType(
      Array(
        StructField(
          "Level1ColumnA",
          StructType(
            Array(
              StructField(
                "Level2ColumnA",
                StructType(
                  Array(StructField("Level3ColumnA", StringType))
                )
              ))
          )
        ),
        StructField("Level1ColumnB", ArrayType(StringType))
      )
    )

    val expectedSchema = StructType(
      Array(
        StructField(
          "LEVEL1COLUMNA",
          StructType(
            Array(
              StructField(
                "LEVEL2COLUMNA",
                StructType(
                  Array(StructField("LEVEL3COLUMNA", StringType))
                )
              ))
          )
        ),
        StructField("LEVEL1COLUMNB", ArrayType(StringType))
      )
    )

    val schemaResult = awesomeService.renameColumnsToUpperCase(someSchema)

    schemaResult shouldBe expectedSchema
  }
}
