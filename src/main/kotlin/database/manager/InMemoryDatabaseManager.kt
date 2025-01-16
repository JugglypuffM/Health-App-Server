package database.manager

import database.exception.DatabaseException
import domain.training.Training
import domain.user.Account
import domain.user.UserInfo
import java.time.LocalDate
import java.util.*

object InMemoryDatabaseManager : DatabaseManager {
    private val accounts = mutableMapOf<String, Account>()
    private val userInformation = mutableMapOf<String, UserInfo>()
    private val trainings = mutableMapOf<String, MutableList<Training>>()
    override fun addAccount(account: Account) {
        if (!accounts.containsKey(account.login)) {
            accounts[account.login] = account
            userInformation[account.login] = UserInfo()
        } else {
            throw DatabaseException("Account already exist")
        }
    }

    override fun deleteAccount(login: String) {
        if (accounts.containsKey(login)) {
            accounts.remove(login)
        } else {
            throw DatabaseException("Account not exist")
        }
    }

    override fun updateAccount(login: String, account: Account) {
        if (accounts.containsKey(login)) {
            accounts[login] = account
        } else {
            throw DatabaseException("Account not exist")
        }
    }

    override fun getAccount(login: String): Optional<Account> {
        return Optional.ofNullable(accounts[login])
    }

    override fun updateUserInformation(login: String, userInfo: UserInfo) {
        if (accounts.containsKey(login)) {
            userInformation[login] = userInfo
        } else {
            throw DatabaseException("User not exist")
        }
    }

    override fun getUserInformation(login: String): Optional<UserInfo> {
        return Optional.ofNullable(userInformation[login])
    }

    override fun saveTraining(login: String, training: Training) {
        if (!accounts.containsKey(login)) {
            throw DatabaseException("Account not exist")
        }

        val existingTrainings = trainings[login]
        if (existingTrainings is MutableList<Training>) {
            existingTrainings.add(training)
        } else {
            trainings[login] = mutableListOf(training)
        }
    }

    override fun getTrainingsOnDate(login: String, date: LocalDate): List<Training> {
        if (!accounts.containsKey(login)) {
            throw DatabaseException("Account not exist")
        }

        return trainings[login]?.filter { it.date == date } ?: emptyList()
    }

    override fun deleteTrainingById(id: Long) {
        for ((_, trainingList) in trainings) {
            val trainingToRemove = trainingList.find { it.id == id }
            if (trainingToRemove != null) {
                trainingList.remove(trainingToRemove)
                return
            }
        }
        throw DatabaseException("Training with id $id not found")
    }

    fun dropDataBase() {
        accounts.clear()
        userInformation.clear()
        trainings.clear()
    }
}