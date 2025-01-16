package database.exception

/**
 * Ошибка выполнения запросов к базе данных
 */
class DatabaseException(message: String, cause: Throwable? = null) : Exception(message, cause)