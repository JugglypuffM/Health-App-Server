package services.data

import com.google.protobuf.Empty
import database.manager.DatabaseManager
import domain.auth.AuthResult
import domain.auth.ResultCode
import domain.user.UserInfo
import grpc.DataProto
import grpc.DataProto.UserDataRequest
import grpc.DataProto.UserDataResponse
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import services.auth.Authenticator
import java.util.Optional

class DataServiceImplTest {

    private lateinit var authenticatorMock: Authenticator
    private lateinit var databaseManagerMock: DatabaseManager
    private lateinit var dataServiceImpl: DataServiceImpl
    private lateinit var responseObserver: StreamObserver<UserDataResponse>
    private lateinit var emptyObserver: StreamObserver<Empty>

    @BeforeEach
    fun setUp() {
        authenticatorMock = mockk()
        databaseManagerMock = mockk()
        responseObserver = mockk(relaxed = true)
        emptyObserver = mockk(relaxed = true)
        dataServiceImpl = DataServiceImpl(authenticatorMock, databaseManagerMock)
    }

    @Test
    fun `getUserData should return failure when no credentials provided`() {
        val request = UserDataRequest.newBuilder().build()

        val expectedData = DataProto.UserData.newBuilder()
            .setName("")
            .setAge(0)
            .setWeight(0)
            .setTotalDistance(0)
            .build()

        every { authenticatorMock.login("", "") } returns AuthResult(
            ResultCode.INVALID_CREDENTIALS,
            "invalid creds"
        )

        dataServiceImpl.getUserData(request, responseObserver)

        verify {responseObserver.onNext(UserDataResponse.newBuilder().setSuccess(false).setData(expectedData).build()) }
        verify {responseObserver.onCompleted() }
    }

    @Test
    fun `getUserData should return success when authentication is successful`() {
        val request = UserDataRequest.newBuilder()
            .setLogin("testUser")
            .setPassword("testPass")
            .build()
        val userInfo = UserInfo("testName", 20, 80, 100)

        val expectedData = DataProto.UserData.newBuilder()
            .setName("testName")
            .setAge(20)
            .setWeight(80)
            .setTotalDistance(100)
            .build()

        every { authenticatorMock.login("testUser", "testPass") } returns AuthResult(ResultCode.OPERATION_SUCCESS, "")
        every { databaseManagerMock.getUserInformation("testUser") } returns Optional.of(userInfo)
        every { authenticatorMock.login("testUser", "testPass") } returns AuthResult(
            ResultCode.OPERATION_SUCCESS,
            "success"
        )

        dataServiceImpl.getUserData(request, responseObserver)

        verify {responseObserver.onNext(UserDataResponse.newBuilder().setSuccess(true).setData(expectedData).build()) }
        verify {responseObserver.onCompleted() }
    }

    @Test
    fun `getUserData should return failure when credentials are invalid`() {
        val request = UserDataRequest.newBuilder()
            .setLogin("testUser")
            .setPassword("wrongPass")
            .build()

        val expectedData = DataProto.UserData.newBuilder()
            .setName("")
            .setAge(0)
            .setWeight(0)
            .setTotalDistance(0)
            .build()

        every { authenticatorMock.login("testUser", "wrongPass") } returns AuthResult(ResultCode.INVALID_CREDENTIALS, "")
        every { databaseManagerMock.getUserInformation("testUser") } returns Optional.of(UserInfo("John", 1000-7, 993-7, 986-7))
        every { authenticatorMock.login("testUser", "wrongPass") } returns AuthResult(
            ResultCode.INVALID_CREDENTIALS,
            "invalid creds"
        )

        dataServiceImpl.getUserData(request, responseObserver)

        verify {responseObserver.onNext(UserDataResponse.newBuilder().setSuccess(false).setData(expectedData).build()) }
        verify {responseObserver.onCompleted() }
    }

    @Test
    fun `updateUserData should return failure when no data provided`() {
        val request = DataProto.UpdateDataRequest.newBuilder().setLogin("testUser").setPassword("testPass").build()

        every { authenticatorMock.login("testUser", "testPass") } returns AuthResult(
            ResultCode.OPERATION_SUCCESS,
            "success"
        )

        dataServiceImpl.updateUserData(request, emptyObserver)

        verify {
            emptyObserver.onError(
                match{ Status.fromThrowable(it).code == Status.Code.INVALID_ARGUMENT }
            )
        }
    }

    @Test
    fun `updateUserData should return success when authentication is successful`() {
        val oldUserInfo = UserInfo("oldName", 21, 100, 100)
        val newUserInfo = UserInfo("testName", 20, 80, 100000)
        val resultUserInfo = UserInfo("testName", 20, 80, 100)

        val request = DataProto.UpdateDataRequest.newBuilder()
            .setLogin("testUser")
            .setPassword("testPass")
            .setData(newUserInfo.toUserData())
            .build()

        every { databaseManagerMock.getUserInformation("testUser") } returns Optional.of(oldUserInfo)
        every { databaseManagerMock.updateUserInformation("testUser", resultUserInfo) } just Runs
        every { authenticatorMock.login("testUser", "testPass") } returns AuthResult(
            ResultCode.OPERATION_SUCCESS,
            "success"
        )

        dataServiceImpl.updateUserData(request, emptyObserver)

        verify {emptyObserver.onNext(Empty.getDefaultInstance()) }
    }

    @Test
    fun `updateUserData should return failure when credentials are invalid`() {
        val userInfo = UserInfo("testName", 20, 80, 100)

        val request = DataProto.UpdateDataRequest.newBuilder()
            .setLogin("testUser")
            .setPassword("wrongPass")
            .setData(userInfo.toUserData())
            .build()

        every { databaseManagerMock.updateUserInformation("testUser", userInfo) } just Runs
        every { authenticatorMock.login("testUser", "wrongPass") } returns AuthResult(
            ResultCode.INVALID_CREDENTIALS,
            "success"
        )

        dataServiceImpl.updateUserData(request, emptyObserver)

        verify {
            emptyObserver.onError(
                match{ Status.fromThrowable(it).code == Status.Code.UNAUTHENTICATED }
            )
        }
    }
}
