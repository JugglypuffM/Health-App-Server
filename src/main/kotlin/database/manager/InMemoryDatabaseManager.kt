package database.manager

import database.dao.DAO
import domain.training.Training
import domain.user.Account
import domain.user.UserInfo
import java.time.LocalDate
import java.util.*

object InMemoryDatabaseManager : DatabaseManager {
    private val accounts = mutableMapOf<String, Account>()
    private val userInformation = mutableMapOf<String, UserInfo>()
    override fun addAccount(account: Account) {
        if (!accounts.containsKey(account.login)) {
            accounts[account.login] = account
            userInformation[account.login] = UserInfo()
        } else {
            throw DAO.DatabaseException("Account already exist")
        }
    }

    override fun deleteAccount(login: String) {
        if (accounts.containsKey(login)) {
            accounts.remove(login)
        } else {
            throw DAO.DatabaseException("Account not exist")
        }
    }

    override fun updateAccount(login: String, account: Account) {
        if (accounts.containsKey(login)) {
            accounts[login] = account
        } else {
            throw DAO.DatabaseException("Account not exist")
        }
    }

    override fun getAccount(login: String): Optional<Account> {
        return Optional.ofNullable(accounts[login])
    }

    override fun updateUserInformation(login: String, userInfo: UserInfo) {
        if (accounts.containsKey(login)) {
            userInformation[login] = userInfo
        } else {
            throw DAO.DatabaseException("User not exist")
        }
    }

    override fun getUserInformation(login: String): Optional<UserInfo> {
        return Optional.ofNullable(userInformation[login])
    }

    override fun saveTraining(login: String, training: Training) {
        TODO("Not yet implemented")
    }

    override fun getTrainingsOnDate(
        login: String,
        date: LocalDate
    ): List<Training> {
        TODO("Not yet implemented")
    }

    fun dropDataBase() {
        accounts.clear()
        userInformation.clear()
    }
}