import database.dao.DAO
import database.manager.InMemoryDatabaseManager
import domain.Account
import domain.UserInfo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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

        val exception = assertThrows<DAO.DatabaseException> {
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
        val exception = assertThrows<DAO.DatabaseException> {
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

        val exception = assertThrows<DAO.DatabaseException> {
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

        val exception = assertThrows<DAO.DatabaseException> {
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
}
