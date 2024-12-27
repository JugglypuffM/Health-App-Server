package database.dao

import database.exception.DatabaseException
import database.tables.training.TrainingTable
import database.tables.training.TrainingTypesTable
import database.tables.training.types.JoggingTable
import database.tables.training.types.YogaTable
import database.tables.user.UsersTable
import domain.training.Training
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.jodatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.LocalDate

class TrainingDAO{
    init {
        val trainingNames : ArrayList<String> = arrayListOf("Jogging", "Yoga")
        transaction {
            try {
                SchemaUtils.create(TrainingTable, TrainingTypesTable, JoggingTable, YogaTable)
                for (name in trainingNames) {
                    TrainingTypesTable.insertIgnore { it[fullName] = name }
                }
            } catch (_: Exception) {
                //TODO Добавить логирование
            }
        }
    }
    fun get(login: String, date: java.time.LocalDate): List<Training> {
        val trainingList: MutableList<Training> = mutableListOf()
        var training : Training
        transaction {
            try {
                for (entry in TrainingTable.innerJoin(UsersTable).selectAll().where {
                    (UsersTable.login.eq(login)) and (TrainingTable.workoutDate.date()
                        .eq(LocalDate(date.year, date.monthValue, date.dayOfMonth).toDateTimeAtStartOfDay()))}) {
                    val dateTime = entry[TrainingTable.workoutDate]
                    when (entry[TrainingTypesTable.fullName]) {
                        "Jogging" -> {
                            val distance = JoggingTable.select(JoggingTable.distance).where {JoggingTable.trainingId eq entry[TrainingTable.id]}
                            training = Training.Jogging(
                                id = entry[TrainingTable.id].value,
                                date = java.time.LocalDate.of(dateTime.year, dateTime.monthOfYear, dateTime.dayOfMonth),
                                duration = java.time.Duration.ofSeconds(entry[TrainingTable.workoutDuration]),
                                distance = distance.map {it[JoggingTable.distance]}.single().toDouble()
                            )
                            trainingList.add(training)
                        }
                        "Yoga" -> {
                            training = Training.Yoga(
                                id = entry[TrainingTable.id].value,
                                date = java.time.LocalDate.of(dateTime.year, dateTime.monthOfYear, dateTime.dayOfMonth),
                                duration = java.time.Duration.ofSeconds(entry[TrainingTable.workoutDuration])
                                )
                            trainingList.add(training)
                        }
                    }
                }
            } catch (e: Exception){
                throw DatabaseException("Error fetching training on date: $date", e)
            }
        }
        return trainingList
    }

    fun delete(id: Long) {
        transaction {
            try {
                TrainingTable.deleteWhere { TrainingTable.id.eq(id) }
            } catch (e: Exception) {
                throw DatabaseException("The training was not found with id: $id", e)
            }
        }
    }

    fun add(login: String, training: Training) {
        transaction {
            try {
                val trainingId = TrainingTable.insertAndGetId {
                    it[loginID] = select(UsersTable.id)
                        .where { UsersTable.login.eq(login)}
                        .map { it[UsersTable.id] }
                        .single()
                    it[workoutDate] = DateTime(training.date)
                    it[workoutDuration] = training.duration.seconds
                    when (training) {
                        is Training.Jogging -> {
                            it[workoutType] = select(TrainingTypesTable.id)
                                .where {TrainingTypesTable.fullName eq "Jogging"}
                                .map { it[TrainingTypesTable.id] }
                                .single()

                        }
                        is Training.Yoga -> it[workoutType] = select(TrainingTypesTable.id)
                            .where {TrainingTypesTable.fullName eq "Yoga"}
                            .map { it[TrainingTypesTable.id] }
                            .single()
                    }
                }
                when (training) {
                    is Training.Jogging -> {
                        JoggingTable.insert {
                            it[JoggingTable.trainingId] = trainingId.value
                            it[distance] = training.distance.toInt()
                        }
                    }
                    is Training.Yoga -> {
                        YogaTable.insert {
                            it[YogaTable.trainingId] = trainingId.value
                        }
                    }
                }
            } catch (e: Exception) {
                throw DatabaseException("Error adding training", e)
            }
        }
    }
}