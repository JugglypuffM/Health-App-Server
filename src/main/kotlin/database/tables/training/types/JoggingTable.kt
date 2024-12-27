package database.tables.training.types

import database.tables.training.TrainingTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

object JoggingTable : LongIdTable() {
    val trainingId = optReference("FK_TrainingJogging", TrainingTable.id)
    val distance : Column<Int> = integer("distance")
}