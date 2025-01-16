package services.training

import database.manager.DatabaseManager
import domain.training.Training
import grpc.TrainingProto
import io.grpc.Context
import io.grpc.stub.StreamObserver
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

class TrainingServiceImplTest {

    private lateinit var loginKeyMock: Context.Key<String>
    private lateinit var databaseManager: DatabaseManager
    private lateinit var responseObserver: StreamObserver<TrainingProto.TrainingsResponse>
    private lateinit var service: TrainingServiceImpl

    @BeforeEach
    fun setUp() {
        loginKeyMock = mockk()
        databaseManager = mockk()
        responseObserver = mockk(relaxed = true)
        service = TrainingServiceImpl(databaseManager, loginKeyMock)
    }

//    @Test
//    fun `saveTraining - should save training successfully`() {
//        val date = LocalDate.now()
//        val request = TrainingProto.SaveRequest.newBuilder()
//            .setLogin("user1")
//            .setPassword("password")
//            .setTraining(
//                TrainingProto.Training.newBuilder()
//                    .setYoga(
//                        TrainingProto.Yoga.newBuilder()
//                            .setDate(com.google.protobuf.Timestamp.newBuilder().setSeconds(date.toEpochDay()))
//                            .setDuration(com.google.protobuf.Duration.newBuilder().setSeconds(3600))
//                            .build()
//                    )
//                    .build()
//            )
//            .build()
//
//        val responseObserver = mockk<StreamObserver<Empty>>(relaxed = true)
//
//        every { authenticator.login("user1", "password") } returns AuthResult(ResultCode.OPERATION_SUCCESS, "Success")
//        every { databaseManager.saveTraining("user1", Training.Yoga(date, Duration.ofSeconds(3600))) } just Runs
//
//        service.saveTraining(request, responseObserver)
//
//        verify { databaseManager.saveTraining("user1", eq(Training.Yoga(date, Duration.ofSeconds(3600)))) }
//        verify { responseObserver.onNext(Empty.getDefaultInstance()) }
//        verify { responseObserver.onCompleted() }
//    }

    @Test
    fun `getTrainings - should return trainings successfully`() {
        val date = LocalDate.of(2024, 12, 6)
        val grpcDate = com.google.protobuf.Timestamp.newBuilder()
            .setSeconds(date.toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.UTC))
            .build()

        val trainings = listOf(Training.Yoga(date, Duration.ofSeconds(1000)), Training.Jogging(date, Duration.ofSeconds(1000), 1))

        every { loginKeyMock.get() } returns "testUser"
        every { databaseManager.getTrainingsOnDate("testUser", date) } returns trainings

        service.getTrainings(grpcDate, responseObserver)

        verify {
            responseObserver.onNext(withArg {
                assert(it.trainingsCount == 2)
            })
            responseObserver.onCompleted()
        }
    }
}
