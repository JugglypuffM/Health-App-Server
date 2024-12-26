package database.dao


import database.tables.UsersTable
import domain.Account
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


class AccountDAO : DAO<Account> {

    init {
        transaction {
            try {
                SchemaUtils.create(UsersTable)
            } catch (_: Exception) {
                //TODO Добавить логирование
            }
        }
    }

    override fun get(login: String): Optional<Account> {
        lateinit var account: Account
        transaction {
            try {
                for (entry in UsersTable.selectAll().where { UsersTable.login.eq(login) }) {
                    account = Account(entry[UsersTable.login], entry[UsersTable.password])
                }
            } catch (e: Exception){
                throw DAO.DatabaseException("Error fetching account with login: $login", e)
            }
        }
        return Optional.ofNullable(account)
    }

    override fun delete(login: String) {
        transaction {
            try {
                UsersTable.deleteWhere { UsersTable.login.eq(login) }
            } catch (e: Exception) {
                throw DAO.DatabaseException("Account not exist with login: $login", e)
            }
        }
    }

    override fun update(login: String, entry: Account) {
        transaction {
            try {
                UsersTable.update({ UsersTable.login.eq(login)}) {
                    it[UsersTable.login] = entry.login
                    it[password] = entry.password
                }
            } catch (e: Exception) {
                throw DAO.DatabaseException("User not exist with login: $login", e)
            }
        }
    }

    override fun add(entry: Account) {
        transaction {
            try {
                UsersTable.insert {
                    it[login] = entry.login
                    it[password] = entry.password
                }
            } catch (e: Exception) {
                throw DAO.DatabaseException("Error adding account: $entry", e)
            }
        }
    }
}