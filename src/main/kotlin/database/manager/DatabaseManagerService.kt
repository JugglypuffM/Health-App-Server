package database.manager

import database.dao.AccountDAO
import database.dao.TrainingDAO
import database.dao.UserInformationDAO
import domain.training.Training
import domain.user.Account
import domain.user.UserInfo
import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.Database
import java.time.LocalDate
import java.util.Optional

/**
 * Реализация менеджера для выполнения запросов к базе данных через Exposed-ORM
 */
object DatabaseManagerService: DatabaseManager {
    private val url = dotenv()["PSQL_URL"].toString()
    private val dbUser = dotenv()["PSQL_USER"].toString()
    private val dbPassword = dotenv()["PSQL_PASS"].toString()
    private val accountDAO : AccountDAO
    private val userInformationDAO : UserInformationDAO
    private val trainingDAO: TrainingDAO
    init {
        Database.connect(
            url = url,
            driver = "org.postgresql.Driver",
            user = dbUser,
            password = dbPassword
        )
        accountDAO = AccountDAO()
        userInformationDAO = UserInformationDAO()
        trainingDAO = TrainingDAO()
    }

    /**
     * Метод для внесения нового пользователя в базу данных
     */
    override fun addAccount(account: Account) {
        accountDAO.add(account)
    }

    /**
     * Метод для удаления всех данных о пользователе
     */
    override fun deleteAccount(login: String) {
        accountDAO.delete(login)
    }

    /**
     * Метод для обновления учётной записи пользователя
     */
    override fun updateAccount(login: String, account: Account) {
        accountDAO.update(login, account)
    }

    /**
     * Метод для получения учётной записи пользователя
     */
    override fun getAccount(login: String) : Optional<Account> {
        return accountDAO.get(login)
    }

    /**
     * Метод для обновления информации о пользователе
     */
    override fun updateUserInformation(login: String, userInfo: UserInfo) {
        userInformationDAO.update(login, userInfo)
    }

    /**
     * Метод для получения дополнительной информации об аккаунте пользователя
     */
    override fun getUserInformation(login: String) : Optional<UserInfo> {
        return userInformationDAO.get(login)
    }

    override fun saveTraining(login: String, training: Training) {
        trainingDAO.add(login, training)
    }

    override fun getTrainingsOnDate(login: String, date: LocalDate): List<Training> {
        return trainingDAO.get(login, date)
    }

    override fun deleteTrainingById(id: Long) {
        trainingDAO.delete(id)
    }


}