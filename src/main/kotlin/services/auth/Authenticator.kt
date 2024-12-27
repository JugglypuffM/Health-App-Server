package services.auth

import database.exception.DatabaseException
import database.manager.DatabaseManager
import domain.user.Account
import domain.auth.ResultCode

class Authenticator(private val databaseManager: DatabaseManager): AuthenticatorInterface {

    override fun register(account: Account): ResultCode {
        try {
            databaseManager.addAccount(account)
            return ResultCode.OPERATION_SUCCESS
        }catch (_: DatabaseException) {
            return ResultCode.USER_ALREADY_EXISTS
        }
    }

    override fun login(login: String, password: String): ResultCode =
        databaseManager.getAccount(login).map<ResultCode> {
            if (it.password == password) {
                ResultCode.OPERATION_SUCCESS
            } else ResultCode.INVALID_CREDENTIALS
        }.orElse(ResultCode.INVALID_CREDENTIALS)
}