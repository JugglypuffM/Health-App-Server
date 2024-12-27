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
        lateinit var account: Account
        transaction {
            try {
                for (entry in UsersTable.selectAll().where { UsersTable.login.eq(login) }) {
                    account = Account(entry[UsersTable.login], entry[UsersTable.password])
                }
            } catch (e: Exception){
                throw DatabaseException("Error fetching account with login: $login", e)
            }
        }
        return Optional.ofNullable(account)
    }

    fun delete(login: String) {
        transaction {
            try {
                UsersTable.deleteWhere { UsersTable.login.eq(login) }
            } catch (e: Exception) {
                throw DatabaseException("Account not exist with login: $login", e)
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
                throw DatabaseException("User not exist with login: $login", e)
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
                throw DatabaseException("Error adding account: $account", e)
            }
        }
    }

}