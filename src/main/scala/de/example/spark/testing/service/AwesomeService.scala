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

import org.apache.spark.sql.types.{ArrayType, StructField, StructType}

class AwesomeService {

  def renameColumnsToUpperCase(schema: StructType): StructType =
    renameAllCols(schema, toUpperCase)

  private def renameAllCols(schema: StructType, rename: String => String): StructType = {

    def recurRename(schema: StructType): Seq[StructField] =
      schema.fields
        .map {
          case StructField(name, dataType: StructType, nullable, meta) =>
            StructField(rename(name), StructType(recurRename(dataType)), nullable, meta)

          case StructField(name, dataType: ArrayType, nullable, meta)
              if dataType.elementType.isInstanceOf[StructType] =>
            StructField(
              rename(name),
              ArrayType(
                StructType(recurRename(dataType.elementType.asInstanceOf[StructType])),
                nullable
              ),
              nullable,
              meta
            )

          case StructField(name, dtype, nullable, meta) =>
            StructField(rename(name), dtype, nullable, meta)
        }

    StructType(recurRename(schema))
  }

  private def toUpperCase(str: String): String =
    str.toUpperCase
}
