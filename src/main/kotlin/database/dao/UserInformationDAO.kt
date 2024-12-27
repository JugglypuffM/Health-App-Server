package database.dao

import database.exception.DatabaseException
import database.tables.user.UsersTable
import domain.user.UserInfo
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.*

class UserInformationDAO {
    init {
        transaction {
            try {
                SchemaUtils.create(UsersTable)
            } catch (_: Exception) {
                //TODO Добавить логирование
            }
        }
    }

    fun get(login: String): Optional<UserInfo> {
        var userInfo: UserInfo? = null
        transaction {
            try {
                for (entry in UsersTable.selectAll().where { UsersTable.login.eq(login) }) {
                    userInfo = UserInfo(
                        name = entry[UsersTable.name],
                        age = entry[UsersTable.age],
                        weight = entry[UsersTable.weight],
                        distance = entry[UsersTable.distance],
                        level = entry[UsersTable.level])
                }
            } catch (e: Exception){
                throw DatabaseException("Error fetching user with id: $id", e)
            }
        }
        return Optional.ofNullable(userInfo)
    }

    fun update(login: String, entry: UserInfo) {
        transaction {
            try {
                UsersTable.update({ UsersTable.login.eq(login) }) {
                    if (entry.name is String && entry.name.isNotBlank()) {
                        it[name] = entry.name
                    }
                    if (entry.age is Int && entry.age > 0) {
                        it[age] = entry.age
                    }
                    if (entry.weight is Int && entry.weight > 0) {
                        it[weight] = entry.weight
                    }
                    if (entry.distance is Int && entry.distance > 0) {
                        it[distance] = entry.distance
                    }
                    if (entry.level is Int && entry.level > 0){
                        it[level] = entry.level
                    }
                }
            } catch (e: Exception) {
                throw DatabaseException("Information not exist for user with login: $login", e)
            }
        }
    }
}
