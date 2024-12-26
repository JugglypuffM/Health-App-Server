package database.dao

import database.tables.UsersTable
import domain.UserInfo
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.*

class UserInformationDAO : DAO<UserInfo> {
    init {
        transaction {
            try {
                SchemaUtils.create(UsersTable)
            } catch (_: Exception) {
                //TODO Добавить логирование
            }
        }
    }

    override fun get(login: String): Optional<UserInfo> {
        return transaction {
            UsersTable.selectAll().where { UsersTable.login.eq(login) }
                .map { entry ->
                    UserInfo(
                        name = entry[UsersTable.name].takeIf { it.isNotEmpty() },
                        age = entry[UsersTable.age].takeIf { it > 0 },
                        weight = entry[UsersTable.weight].takeIf { it > 0 },
                        distance = entry[UsersTable.distance].takeIf { it > 0 }
                    )
                }
                .firstOrNull()
        }.let { Optional.ofNullable(it) }
    }

    override fun update(login: String, entry: UserInfo) {
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
                }
            } catch (e: Exception) {
                throw DAO.DatabaseException("Information not exist for user with login: $login", e)
            }
        }
    }

    override fun add(entry: UserInfo) {
        // Реализация добавления пользователя, если необходимо
    }

    override fun delete(login: String) {
        // Реализация удаления пользователя, если необходимо
    }
}
