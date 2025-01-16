package database.tables.training.types

import database.tables.training.TrainingTable
import org.jetbrains.exposed.dao.id.LongIdTable

object PlankTable : LongIdTable() {
    val trainingId = optReference(name="FK_TrainingPlank", TrainingTable.id)
}