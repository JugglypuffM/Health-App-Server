package database.tables.training.types

import database.tables.training.TrainingTable
import org.jetbrains.exposed.dao.id.LongIdTable

object YogaTable : LongIdTable() {
    val trainingId = optReference(name="FK_TrainingYoga", TrainingTable.id)
}