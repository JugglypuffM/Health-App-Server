package domain.user

/**
 * Люгин и пароль пользователя
 */
data class Account(
    val login: String,
    val password: String,
){
    init {
        require(login.isNotBlank()) { "Login cannot be blank" }
        require(password.length >= 6) { "Password must be at least 6 characters long" }
    }

    override fun toString(): String {
        return "domain.user.Account(login='$login', password='$password')"
    }
}
