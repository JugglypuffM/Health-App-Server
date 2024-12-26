package domain

import grpc.DataProto.UserData

/**
 * Класс с информацией о пользователе
 */
data class UserInfo(
    val name: String? = null,
    val age: Int? = null,
    val weight: Int? = null,
    val distance: Int? = null,
) {
    constructor(userData: UserData) : this(
        userData.name,
        userData.age,
        userData.weight,
        userData.totalDistance
    )

    fun toUserData() = UserData.newBuilder()
        .setName(name?:"")
        .setAge(age?:0)
        .setWeight(weight?:0)
        .setTotalDistance(distance?:0)
        .build()
}
