package services.auth

import com.google.protobuf.Empty
import domain.auth.ResultCode
import domain.user.Account
import grpc.AuthProto
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthServiceTest {

    private lateinit var authenticator: Authenticator
    private lateinit var authService: AuthServiceImpl
    private lateinit var responseObserver: StreamObserver<Empty>

    @BeforeEach
    fun setup() {
        authenticator = mockk()
        responseObserver = mockk(relaxed = true)

        authService = AuthServiceImpl(authenticator)
    }

    @Test
    fun `register - successful registration`() {
        val account = Account("johndoe", "password123")
        val request = AuthProto.AuthRequest.newBuilder()
            .setLogin(account.login)
            .setPassword(account.password)
            .build()

        every { authenticator.register(account) } returns ResultCode.OPERATION_SUCCESS

        authService.register(request, responseObserver)

        verify { responseObserver.onNext(Empty.getDefaultInstance()) }
        verify { responseObserver.onCompleted() }
    }

    @Test
    fun `register - user already exists`() {
        val account = Account("johndoe", "password123")
        val request = AuthProto.AuthRequest.newBuilder()
            .setLogin(account.login)
            .setPassword(account.password)
            .build()

        every { authenticator.register(account) } returns ResultCode.USER_ALREADY_EXISTS

        authService.register(request, responseObserver)

        verify { responseObserver.onError(withArg { assert(it is StatusRuntimeException && it.status.code == Status.ALREADY_EXISTS.code) }) }
    }

    @Test
    fun `register - invalid password (too short)`() {
        val request = AuthProto.AuthRequest.newBuilder()
            .setLogin("johndoe")
            .setPassword("pass")
            .build()

        authService.register(request, responseObserver)

        verify { responseObserver.onError(withArg { assert(it is StatusRuntimeException && it.status.code == Status.INVALID_ARGUMENT.code) }) }
    }

    @Test
    fun `login - successful login`() {
        val login = "johndoe"
        val password = "password123"
        val request = AuthProto.AuthRequest.newBuilder()
            .setLogin(login)
            .setPassword(password)
            .build()

        every { authenticator.login(login, password) } returns ResultCode.OPERATION_SUCCESS

        authService.login(request, responseObserver)

        verify { responseObserver.onNext(Empty.getDefaultInstance()) }
        verify { responseObserver.onCompleted() }
    }

    @Test
    fun `login - invalid login or password`() {
        val login = "johndoe"
        val password = "wrongpassword"
        val request = AuthProto.AuthRequest.newBuilder()
            .setLogin(login)
            .setPassword(password)
            .build()

        every { authenticator.login(login, password) } returns ResultCode.INVALID_CREDENTIALS

        authService.login(request, responseObserver)

        verify { responseObserver.onError(withArg { assert(it is StatusRuntimeException && it.status.code == Status.UNAUTHENTICATED.code ) }) }
    }
}
