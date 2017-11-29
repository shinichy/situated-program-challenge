package models

import io.getquill.{PostgresJdbcContext, SnakeCase}

trait PostgresContext {
  val ctx = new PostgresJdbcContext(SnakeCase, "ctx")
}
