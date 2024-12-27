package database.manager

import database.dao.DAO
import database.dao.AccountDAO
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
    private val url = "jdbc:postgresql://localhost:${dotenv()["DB_PORT"]}/${dotenv()["DB_NAME"]}"
    private val dbUser = dotenv()["PSQL_USER"].toString()
    private val dbPassword = dotenv()["PSQL_PASS"].toString()
    private val dbTableAccounts : DAO<Account>
    private val dbTableUsers : DAO<UserInfo>
    init {
        Database.connect(
            url = url,
            driver = "org.postgresql.Driver",
            user = dbUser,
            password = dbPassword
        )
        dbTableAccounts = AccountDAO()
        dbTableUsers = UserInformationDAO()
    }

    /**
     * Метод для внесения нового пользователя в базу данных
     */
    override fun addAccount(account: Account) {
        dbTableAccounts.add(account)
    }

    /**
     * Метод для удаления всех данных о пользователе
     */
    override fun deleteAccount(login: String) {
        dbTableAccounts.delete(login)
    }

    /**
     * Метод для обновления учётной записи пользователя
     */
    override fun updateAccount(login: String, account: Account) {
        dbTableAccounts.update(login, account)
    }

    /**
     * Метод для получения учётной записи пользователя
     */
    override fun getAccount(login: String) : Optional<Account> {
        return dbTableAccounts.get(login)
    }

    /**
     * Метод для обновления информации о пользователе
     */
    override fun updateUserInformation(login: String, userInfo: UserInfo) {
        dbTableUsers.update(login, userInfo)
    }

    /**
     * Метод для получения дополнительной информации об аккаунте пользователя
     */
    override fun getUserInformation(login: String) : Optional<UserInfo> {
        return dbTableUsers.get(login)
    }

    override fun saveTraining(login: String, training: Training) {
        TODO("Not yet implemented")
    }

    override fun getTrainingsOnDate(login: String, date: LocalDate): List<Training> {
        TODO("Not yet implemented")
    }
}