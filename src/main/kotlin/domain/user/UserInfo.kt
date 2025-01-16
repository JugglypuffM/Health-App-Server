package domain.user

import grpc.DataProto.UserData

/**
 * Класс с информацией о пользователе
 */
data class UserInfo(
    val name: String? = null,
    val age: Int? = null,
    val weight: Int? = null,
    val distance: Int? = null,
    val level: Int? = null,
) {
    constructor(userData: UserData) : this(
        if (userData.name.isEmpty()) null else userData.name,
        if (userData.age <= 0) null else userData.age,
        if (userData.weight <= 0) null else userData.weight
    )

    fun toUserData() = UserData.newBuilder()
        .setName(name?:"")
        .setAge(age?:0)
        .setWeight(weight?:0)
        .setTotalDistance(distance?:0)
        .build()
}
