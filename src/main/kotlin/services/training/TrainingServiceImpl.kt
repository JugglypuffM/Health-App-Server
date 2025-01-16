package services.training

import com.google.protobuf.Empty
import com.google.protobuf.Timestamp
import database.manager.DatabaseManager
import domain.training.Training
import grpc.TrainingProto
import grpc.TrainingServiceGrpc
import io.grpc.Context
import io.grpc.Status
import io.grpc.stub.StreamObserver
import services.auth.AuthInterceptor
import java.time.Instant
import java.time.ZoneId
import java.util.Optional

class TrainingServiceImpl(
    private val databaseManager: DatabaseManager,
    private val loginKey: Context.Key<String> = AuthInterceptor.LOGIN_CONTEXT_KEY
) :
    TrainingServiceGrpc.TrainingServiceImplBase() {
    override fun saveTraining(trainingData: TrainingProto.Training, responseObserver: StreamObserver<Empty>) {
        val training: Optional<Training> = when {
            trainingData.hasYoga() -> Optional.of(Training.Yoga(trainingData.yoga))

            trainingData.hasJogging() -> Optional.of(Training.Jogging(trainingData.jogging))

            trainingData.hasPlank() -> Optional.of(Training.Plank(trainingData.plank))

            else -> Optional.empty()
        }

        val login = loginKey.get()

        training.ifPresentOrElse(
            {
                databaseManager.saveTraining(login, it)

                val info = databaseManager.getUserInformation(login).get()
                when (it) {
                    is Training.Jogging -> databaseManager.updateUserInformation(
                        login,
                        info.copy(distance = (info.distance ?:0) + it.distance)
                    )

                    is Training.Yoga -> {}
                    is Training.Plank -> {}
                }
                responseObserver.onNext(Empty.getDefaultInstance())
                responseObserver.onCompleted()
            },
            {
                responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription("Unexpected training type while parsing training")
                        .asRuntimeException()
                )
            }
        )
    }

    override fun getTrainings(
        date: Timestamp,
        responseObserver: StreamObserver<TrainingProto.TrainingsResponse>
    ) {
        val login = loginKey.get()

        val trainings =
            databaseManager.getTrainingsOnDate(
                login,
                Instant.ofEpochSecond(date.seconds).atZone(ZoneId.systemDefault()).toLocalDate()
            )
        val protoTrainings = trainings.map { it.toTrainingProto() }
        responseObserver.onNext(
            TrainingProto.TrainingsResponse.newBuilder().addAllTrainings(protoTrainings).build()
        )
        responseObserver.onCompleted()
    }
}