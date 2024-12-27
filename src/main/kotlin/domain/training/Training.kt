package domain.training

import com.google.protobuf.Timestamp
import grpc.TrainingProto
import java.time.Duration
import java.time.LocalDate

/**
 * Информация о тренировке
 * Специфична для каждого типа тренировки
 */
sealed class Training(
    val date: LocalDate,
    val duration: Duration
) {
    /**
     * Информация о йоге
     */
    class Yoga(date: LocalDate, duration: Duration) : Training(
        date = date,
        duration = duration
    ) {
        constructor(yoga: TrainingProto.Yoga) : this(
            LocalDate.ofEpochDay(yoga.date.seconds),
            Duration.ofSeconds(yoga.duration.seconds)
        )

        override fun toTrainingProto(): TrainingProto.Training {
            return TrainingProto.Training.newBuilder()
                .setYoga(
                    TrainingProto.Yoga.newBuilder()
                        .setDate(
                            Timestamp.newBuilder()
                                .setSeconds(date.toEpochDay())
                        )
                        .setDuration(
                            com.google.protobuf.Duration.newBuilder()
                                .setSeconds(duration.seconds)
                        )
                        .build()
                ).build()
        }
    }

    /**
     * Информация о беге
     */
    class Jogging(date: LocalDate, duration: Duration, val distance: Int) : Training(
        date = date,
        duration = duration
    ) {
        constructor(jogging: TrainingProto.Jogging) : this(
            LocalDate.ofEpochDay(jogging.date.seconds),
            Duration.ofSeconds(jogging.duration.seconds),
            jogging.distance
        )

        override fun toTrainingProto(): TrainingProto.Training {
            return TrainingProto.Training.newBuilder()
                .setJogging(
                    TrainingProto.Jogging.newBuilder()
                        .setDate(
                            Timestamp.newBuilder()
                                .setSeconds(date.toEpochDay())
                        )
                        .setDuration(
                            com.google.protobuf.Duration.newBuilder()
                                .setSeconds(duration.seconds)
                        )
                        .setDistance(distance)
                        .build()
                ).build()
        }
    }

    abstract fun toTrainingProto(): TrainingProto.Training
}