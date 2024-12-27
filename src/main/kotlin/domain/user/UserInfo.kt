package domain.user

/**
 * Класс с информацией о пользователе
 */
data class UserInfo(
    val name: String,
    val age: Int,
    val weight: Int,
    val distance: Int,
    val level: Int? = null,
)
