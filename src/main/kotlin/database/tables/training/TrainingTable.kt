package database.tables.training

import database.tables.user.UsersTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.jodatime.date
import org.joda.time.DateTime

object TrainingTable : LongIdTable() {
    val loginID = optReference(name="FK_loginID", UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val workoutType = optReference(name="FK_typeID", TrainingTypesTable.id, onDelete = ReferenceOption.NO_ACTION)
    val workoutDate: Column<DateTime> = date("workout_date")
    val workoutDuration: Column<Long> = long("workout_duration")
}