package database

import database.exception.DatabaseException
import database.manager.InMemoryDatabaseManager
import domain.training.Training
import domain.user.Account
import domain.user.UserInfo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Duration
import java.time.LocalDate

class InMemoryDatabaseManagerTest {

    private lateinit var databaseManager: InMemoryDatabaseManager


    @BeforeEach
    fun setUp() {
        this.databaseManager = InMemoryDatabaseManager
    }

    @Test
    fun testAddAccountSuccessfully() {
        databaseManager.dropDataBase()
        val account = Account(login = "user1", password = "password")
        databaseManager.addAccount(account)

        val retrievedAccount = databaseManager.getAccount("user1")
        assertTrue(retrievedAccount.isPresent)
        assertEquals(account, retrievedAccount.get())
    }

    @Test
    fun testAddAccountThrowsExceptionWhenAccountAlreadyExists() {
        databaseManager.dropDataBase()
        val account = Account(login = "user1", password = "password")
        databaseManager.addAccount(account)

        val exception = assertThrows<DatabaseException> {
            databaseManager.addAccount(account)
        }
        assertEquals("Account already exist", exception.message)
    }

    @Test
    fun testDeleteAccountSuccessfully() {
        databaseManager.dropDataBase()
        val account = Account(login = "user1", password = "password")
        databaseManager.addAccount(account)

        databaseManager.deleteAccount("user1")
        val retrievedAccount = databaseManager.getAccount("user1")
        assertFalse(retrievedAccount.isPresent)
    }

    @Test
    fun testDeleteAccountThrowsExceptionWhenAccountDoesNotExist() {
        databaseManager.dropDataBase()
        val exception = assertThrows<DatabaseException> {
            databaseManager.deleteAccount("nonexistent")
        }
        assertEquals("Account not exist", exception.message)
    }

    @Test
    fun testUpdateAccountSuccessfully() {
        databaseManager.dropDataBase()
        val account = Account(login = "user1", password = "password")
        databaseManager.addAccount(account)

        val updatedAccount = Account(login = "user1", password = "newpassword")
        databaseManager.updateAccount("user1", updatedAccount)

        val retrievedAccount = databaseManager.getAccount("user1")
        assertTrue(retrievedAccount.isPresent)
        assertEquals(updatedAccount, retrievedAccount.get())
    }

    @Test
    fun testUpdateAccountThrowsExceptionWhenAccountDoesNotExist() {
        databaseManager.dropDataBase()
        val updatedAccount = Account(login = "user1", password = "newpassword")

        val exception = assertThrows<DatabaseException> {
            databaseManager.updateAccount("nonexistent", updatedAccount)
        }
        assertEquals("Account not exist", exception.message)
    }

    @Test
    fun testUpdateUserInformationSuccessfully() {
        databaseManager.dropDataBase()
        val account = Account(login = "user1", password = "password")
        databaseManager.addAccount(account)

        val userInfo = UserInfo(name = "John Doe", age = 19, weight = 59, distance = 0)
        databaseManager.updateUserInformation("user1", userInfo)

        val retrievedUserInfo = databaseManager.getUserInformation("user1")
        assertTrue(retrievedUserInfo.isPresent)
        assertEquals(userInfo, retrievedUserInfo.get())
    }

    @Test
    fun testUpdateUserInformationThrowsExceptionWhenUserDoesNotExist() {
        databaseManager.dropDataBase()
        val userInfo = UserInfo(name = "John Doe", age = 19, weight = 59, distance = 0)

        val exception = assertThrows<DatabaseException> {
            databaseManager.updateUserInformation("nonexistent", userInfo)
        }
        assertEquals("User not exist", exception.message)
    }

    @Test
    fun testGetUserInformationReturnsEmptyWhenNoInfoExists() {
        databaseManager.dropDataBase()
        val retrievedUserInfo = databaseManager.getUserInformation("nonexistent")
        assertFalse(retrievedUserInfo.isPresent)
    }

    @Test
    fun testGetEmptyUserInformationIfAccountExist() {
        databaseManager.dropDataBase()
        databaseManager.addAccount(Account("lox", "12345678"))
        val retrievedUserInfo = databaseManager.getUserInformation("lox")
        assertTrue(retrievedUserInfo.isPresent)
        assertEquals(UserInfo(name = null, age = null, weight = null, distance = null), retrievedUserInfo.get())
    }

    @Test
    fun testAddingAndGettingTrainingIfAccountExist() {
        databaseManager.dropDataBase()
        databaseManager.addAccount(Account("lox", "12345678"))
        val expectedTrainings = listOf(
            Training.Jogging(LocalDate.of(2024, 12, 31), Duration.ofSeconds(100), 1000, id=1),
            Training.Yoga(LocalDate.of(2024, 12, 31), Duration.ofSeconds(200), id=2)
        )
        for (training in expectedTrainings) {
            databaseManager.saveTraining("lox", training)
        }
        databaseManager.saveTraining("lox", Training.Jogging(LocalDate.of(2024, 11, 13), Duration.ofSeconds(300), 3000, id=3))

        val savedTrainings = databaseManager.getTrainingsOnDate("lox", LocalDate.of(2024, 12, 31))

        assertTrue(savedTrainings.isNotEmpty())
        assertEquals(2, savedTrainings.size)
        for (expected in expectedTrainings) {
            assertTrue(savedTrainings.contains(expected))
        }
    }

    @Test
    fun testAddingTrainingIfAccountNotExist() {
        databaseManager.dropDataBase()

        assertThrows<DatabaseException> {
            databaseManager.saveTraining("lox", Training.Yoga(LocalDate.of(2024, 11, 13), Duration.ofSeconds(300), id=1))
        }
    }

    @Test
    fun testGettingTrainingsOnDateIfAccountNotExist() {
        databaseManager.dropDataBase()

        assertThrows<DatabaseException> {
            databaseManager.getTrainingsOnDate("lox", LocalDate.of(2024, 12, 31))
        }
    }

    @Test
    fun testDeletingTrainingByIdIfExists() {
        databaseManager.dropDataBase()
        databaseManager.addAccount(Account("lox", "12345678"))

        val training = Training.Jogging(LocalDate.of(2024, 12, 31), Duration.ofSeconds(100), 1000, id=1)
        databaseManager.saveTraining("lox", training)

        databaseManager.deleteTrainingById(1)

        val savedTrainings = databaseManager.getTrainingsOnDate("lox", LocalDate.of(2024, 12, 31))
        assertTrue(savedTrainings.isEmpty())
    }

    @Test
    fun testDeletingTrainingByIdIfNotExists() {
        databaseManager.dropDataBase()
        databaseManager.addAccount(Account("lox", "12345678"))

        assertThrows<DatabaseException> {
            databaseManager.deleteTrainingById(9999)
        }
    }
}
