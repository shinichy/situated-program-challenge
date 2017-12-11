import io.getquill.{PostgresJdbcContext, SnakeCase}

trait JdbcContext {
  lazy val ctx = new PostgresJdbcContext(SnakeCase, "ctx")
}
