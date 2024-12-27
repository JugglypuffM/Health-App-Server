package database.tables.training

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object TrainingTypesTable : IntIdTable() {
    val fullName : Column<String> = varchar("name", 20).uniqueIndex()
}