package services.data

import database.manager.DatabaseManager
import domain.auth.ResultCode
import domain.user.UserInfo
import grpc.DataProto
import grpc.DataProto.UserDataRequest
import grpc.DataProto.UserDataResponse
import grpc.DataServiceGrpc
import io.grpc.stub.StreamObserver
import services.auth.AuthenticatorInterface

class DataServiceImpl(private val authenticator: AuthenticatorInterface, private val databaseManager: DatabaseManager) :
    DataServiceGrpc.DataServiceImplBase() {
    private fun createBasicDataResponse(success: Boolean, info: UserInfo): UserDataResponse {
        val data = DataProto.UserData.newBuilder()
            .setName(info.name)
            .setAge(info.age)
            .setWeight(info.weight)
            .setTotalDistance(info.distance)
            .build()
        return UserDataResponse.newBuilder()
            .setSuccess(success)
            .setData(data)
            .build()
    }

    override fun getUserData(
        request: UserDataRequest,
        responseObserver: StreamObserver<UserDataResponse>
    ) {
        when (authenticator.login(request.login, request.password).resultCode) {
            ResultCode.OPERATION_SUCCESS -> {
                val userInfo = databaseManager.getUserInformation(request.login)
                responseObserver.onNext(
                    createBasicDataResponse(
                        userInfo.isPresent,
                        userInfo.orElse(UserInfo("", 0, 0, 0))
                    )
                )
            }

            else -> responseObserver.onNext(createBasicDataResponse(false, UserInfo("", 0, 0, 0)))
        }
        responseObserver.onCompleted()
    }
}
