package database.dao

import database.exception.DatabaseException
import database.tables.user.UsersTable
import domain.user.UserInfo
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
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
                UsersTable.update({ UsersTable.login.eq(login)}) {
                    it[name] = entry.name
                    it[age] = entry.age
                    it[weight] = entry.weight
                    it[distance] = entry.distance
                    it[level] = entry.level?: 0
                }
            } catch (e: Exception) {
                throw DatabaseException("User not exist with id: $id", e)
            }
        }
    }

}