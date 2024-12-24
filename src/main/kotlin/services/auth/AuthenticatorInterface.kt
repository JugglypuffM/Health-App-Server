package services.auth

import domain.auth.ResultCode
import domain.user.Account

interface AuthenticatorInterface {
    fun register(account: Account): ResultCode

    fun login(login: String, password: String): ResultCode
}