package database.dao


import database.exception.DatabaseException
import database.tables.user.UsersTable
import domain.user.Account
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


class AccountDAO {

    init {
        transaction {
            try {
                SchemaUtils.create(UsersTable)
            } catch (_: Exception) {
                //TODO Добавить логирование
            }
        }
    }

    fun get(login: String): Optional<Account> {
        var account: Account? = null
        transaction {
            try {
                for (entry in UsersTable.selectAll().where { UsersTable.login.eq(login) }) {
                    account = Account(entry[UsersTable.login], entry[UsersTable.password])
                }
            } catch (e: Exception){
                throw DatabaseException("Error fetching user with id: $id", e)
            }
        }
        return Optional.ofNullable(account)
    }

    fun delete(login: String) {
        transaction {
            try {
                UsersTable.deleteWhere { UsersTable.login.eq(login) }
            } catch (e: Exception) {
                throw DatabaseException("User not exist with id: $id", e)
            }
        }
    }

    fun update(login: String, account: Account) {
        transaction {
            try {
                UsersTable.update({ UsersTable.login.eq(login)}) {
                    it[UsersTable.login] = account.login
                    it[password] = account.password
                }
            } catch (e: Exception) {
                throw DatabaseException("User not exist with id: $id", e)
            }
        }
    }

    fun add(account: Account) {
        transaction {
            try {
                UsersTable.insert {
                    it[login] = account.login
                    it[password] = account.password
                }
            } catch (e: Exception) {
                throw DatabaseException("Error adding user with id: $id", e)
            }
        }
    }

}