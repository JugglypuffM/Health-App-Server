package services.data

import com.google.protobuf.Empty
import database.manager.DatabaseManager
import domain.user.UserInfo
import grpc.DataProto
import io.grpc.Context
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional

class DataServiceImplTest {

    private lateinit var loginKeyMock: Context.Key<String>
    private lateinit var databaseManagerMock: DatabaseManager
    private lateinit var dataServiceImpl: DataServiceImpl
    private lateinit var responseObserver: StreamObserver<DataProto.UserData>
    private lateinit var emptyObserver: StreamObserver<Empty>

    @BeforeEach
    fun setUp() {
        loginKeyMock = mockk()
        databaseManagerMock = mockk()
        responseObserver = mockk(relaxed = true)
        emptyObserver = mockk(relaxed = true)
        dataServiceImpl = DataServiceImpl(databaseManagerMock, loginKeyMock)
    }

    @Test
    fun `getUserData should return success`() {
        val userInfo = UserInfo("testName", 20, 80, 100)

        val expectedData = DataProto.UserData.newBuilder()
            .setName("testName")
            .setAge(20)
            .setWeight(80)
            .setTotalDistance(100)
            .build()

        every { loginKeyMock.get() } returns "testUser"
        every { databaseManagerMock.getUserInformation("testUser") } returns Optional.of(userInfo)

        dataServiceImpl.getUserData(Empty.getDefaultInstance(), responseObserver)

        verify { responseObserver.onNext(expectedData) }
        verify {responseObserver.onCompleted() }
    }

    @Test
    fun `getUserData should return unknown when no user is found`() {
        every { loginKeyMock.get() } returns "testUser"
        every { databaseManagerMock.getUserInformation("testUser") } returns Optional.empty<UserInfo>()

        dataServiceImpl.getUserData(Empty.getDefaultInstance(), responseObserver)

        verify {
            responseObserver.onError(
                withArg {
                    assert(it is StatusRuntimeException && it.status.code == Status.UNKNOWN.code)
                }
            )
        }
        verify {responseObserver.onCompleted() }
    }

    @Test
    fun `updateUserData should return success`() {
        val oldUserInfo = UserInfo("oldName", 21, 100, 100)
        val newUserInfo = UserInfo("testName", 20, 80, 100000)
        val resultUserInfo = UserInfo("testName", 20, 80, 100)

        val request = newUserInfo.toUserData()

        every { loginKeyMock.get() } returns "testUser"
        every { databaseManagerMock.getUserInformation("testUser") } returns Optional.of(oldUserInfo)
        every { databaseManagerMock.updateUserInformation("testUser", resultUserInfo) } just Runs

        dataServiceImpl.updateUserData(request, emptyObserver)

        verify {emptyObserver.onNext(Empty.getDefaultInstance()) }
    }

}
