/*
 * Copyright (c) 2024, NVIDIA CORPORATION.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*** spark-rapids-shim-json-lines
{"spark": "330"}
spark-rapids-shim-json-lines ***/
package org.apache.spark.sql.rapids.suites

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.execution.datasources.parquet.ParquetColumnIndexSuite
import org.apache.spark.sql.rapids.utils.RapidsSQLTestsBaseTrait

class RapidsParquetColumnIndexSuite extends ParquetColumnIndexSuite with RapidsSQLTestsBaseTrait{
  private val actions: Seq[DataFrame => DataFrame] = Seq(
    "_1 = 500",
    "_1 = 500 or _1 = 1500",
    "_1 = 500 or _1 = 501 or _1 = 1500",
    "_1 = 500 or _1 = 501 or _1 = 1000 or _1 = 1500",
    "_1 >= 500 and _1 < 1000",
    "(_1 >= 500 and _1 < 1000) or (_1 >= 1500 and _1 < 1600)"
  ).map(f => (df: DataFrame) => df.filter(f))

  testRapids("test reading unaligned pages - test all types") {
    val df = spark
      .range(0, 2000)
      .selectExpr(
        "id as _1",
        "cast(id as short) as _3",
        "cast(id as int) as _4",
        "cast(id as float) as _5",
        "cast(id as double) as _6",
        "cast(id as decimal(20,0)) as _7",
        "cast(cast(1618161925000 + id * 1000 * 60 * 60 * 24 as timestamp) as date) as _9"
      )
    checkUnalignedPages(df)(actions: _*)
  }
}
