package services.data

import com.google.protobuf.Empty
import database.manager.DatabaseManager
import domain.user.UserInfo
import grpc.DataProto
import grpc.DataServiceGrpc
import io.grpc.Context
import io.grpc.Status
import io.grpc.stub.StreamObserver
import services.auth.AuthInterceptor

class DataServiceImpl(
    private val databaseManager: DatabaseManager,
    private val loginKey: Context.Key<String> = AuthInterceptor.LOGIN_CONTEXT_KEY
) :
    DataServiceGrpc.DataServiceImplBase() {

    override fun getUserData(
        request: Empty,
        responseObserver: StreamObserver<DataProto.UserData>
    ) {
        val login = loginKey.get()

        val userInfo = databaseManager.getUserInformation(login)
        userInfo.ifPresentOrElse(
            { responseObserver.onNext(it.toUserData()) },
            {
                responseObserver.onError(
                    Status.UNKNOWN.withDescription("User not found, thou it should have been").asRuntimeException()
                )
            }
        )
        responseObserver.onCompleted()
    }

    override fun updateUserData(request: DataProto.UserData, responseObserver: StreamObserver<Empty>) {
        val login = loginKey.get()

        val currentInfo = databaseManager.getUserInformation(login).orElse(UserInfo("", 0, 0, 0))
        databaseManager.updateUserInformation(login, UserInfo(request).copy(distance = currentInfo.distance))
        responseObserver.onNext(Empty.getDefaultInstance())
        responseObserver.onCompleted()
    }
}
