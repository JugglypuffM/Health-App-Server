package domain.training

import com.google.protobuf.Timestamp
import grpc.TrainingProto
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Информация о тренировке
 * Специфична для каждого типа тренировки
 */
sealed class Training(
    val date: LocalDate,
    val duration: Duration,
    val id: Long? = null
) {
    /**
     * Информация о йоге
     */
    class Yoga(date: LocalDate, duration: Duration, id: Long? = null) : Training(
        date = date,
        duration = duration,
        id = id
    ) {
        constructor(yoga: TrainingProto.Yoga) : this(
            Instant.ofEpochSecond(yoga.date.seconds).atZone(ZoneId.systemDefault()).toLocalDate(),
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
    class Jogging(date: LocalDate, duration: Duration, val distance: Int, id: Long? = null) : Training(
        date = date,
        duration = duration,
        id = id
    ) {
        constructor(jogging: TrainingProto.Jogging) : this(
            Instant.ofEpochSecond(jogging.date.seconds).atZone(ZoneId.systemDefault()).toLocalDate(),
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

    class Plank(date: LocalDate, duration: Duration, id: Long? = null) : Training(
        date=date,
        duration=duration,
        id=id
    ){
        constructor(plank: TrainingProto.Plank) : this(
            Instant.ofEpochSecond(plank.date.seconds).atZone(ZoneId.systemDefault()).toLocalDate(),
            Duration.ofSeconds(plank.duration.seconds)
        )

        override fun toTrainingProto(): TrainingProto.Training {
            return TrainingProto.Training.newBuilder()
                .setPlank(
                    TrainingProto.Plank.newBuilder()
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

        override fun toString(): String {
            return "Plank($date, $duration)"
        }
    }

    abstract fun toTrainingProto(): TrainingProto.Training
}