import io.getquill.{PostgresJdbcContext, SnakeCase}

package object db {

  type DbContext = PostgresJdbcContext[SnakeCase]
}
