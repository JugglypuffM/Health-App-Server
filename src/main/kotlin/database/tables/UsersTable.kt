package database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object UsersTable : IntIdTable() {
    val login : Column<String> = varchar("u_login", 45).uniqueIndex()
    val password : Column<String> = varchar("u_password", 35)
    val name : Column<String> = varchar("u_name", 20).default("")
    val age : Column<Int> = integer("age").default(0)
    val weight : Column<Int> = integer("weight").default(0)
    val distance : Column<Int> = integer("distance").default(0)
}