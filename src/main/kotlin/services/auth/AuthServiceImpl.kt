package services.auth

import com.google.protobuf.Empty
import domain.auth.ResultCode
import domain.user.Account
import grpc.AuthProto
import grpc.AuthServiceGrpc
import io.grpc.Status
import io.grpc.stub.*

class AuthServiceImpl(private val authenticator: AuthenticatorInterface) : AuthServiceGrpc.AuthServiceImplBase() {
    override fun register(request: AuthProto.AuthRequest, responseObserver: StreamObserver<Empty>) {
        try {
            val account = Account(request.login, request.password)
            val authResult = authenticator.register(account)

            when (authResult) {
                ResultCode.OPERATION_SUCCESS -> {
                    responseObserver.onNext(Empty.getDefaultInstance())
                    responseObserver.onCompleted()
                }
                ResultCode.USER_ALREADY_EXISTS ->
                    responseObserver.onError(
                        Status.ALREADY_EXISTS.withDescription("User already exists").asRuntimeException()
                    )

                else -> {}
            }
        }
        catch (_: IllegalArgumentException) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT.withDescription("Login or password validation failed").asRuntimeException()
            )
        }
    }

    override fun login(request: AuthProto.AuthRequest, responseObserver: StreamObserver<Empty>) {
        val authResult = authenticator.login(request.login, request.password)

        when (authResult) {
            ResultCode.OPERATION_SUCCESS -> {
                responseObserver.onNext(Empty.getDefaultInstance())
                responseObserver.onCompleted()
            }

            ResultCode.INVALID_CREDENTIALS ->
                responseObserver.onError(
                    Status.UNAUTHENTICATED.withDescription("Invalid user credentials").asRuntimeException()
                )
            else -> {}
        }
    }
}
