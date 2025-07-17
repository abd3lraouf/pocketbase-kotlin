package dev.abd3lraouf.libs.pocketbase.kotlin.models.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration.Companion.parse
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
public typealias InstantPocketbase =
    @Serializable(InstantPocketbaseSerializer::class)
    Instant

internal fun Int.padded(length: Int): String = this.toString().padStart(length, '0')

@OptIn(ExperimentalTime::class)
public fun Instant.toStringPocketbase(): String {
    val dt = this.toLocalDateTime(TimeZone.UTC)

    return "${dt.year}-${dt.monthNumber.padded(2)}-${dt.dayOfMonth.padded(2)} ${dt.hour.padded(2)}:${
        dt.minute.padded(
            2,
        )
    }:${
        dt.second.padded(
            2,
        )
    }.${dt.nanosecond / 1_000_000}Z"
}

@OptIn(ExperimentalTime::class)
public fun Instant.Companion.parsePocketbase(string: String): InstantPocketbase = Instant.parse(string.replace(' ', 'T'))

@OptIn(ExperimentalTime::class)
public object InstantPocketbaseSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Instant {
        val dateStr = decoder.decodeString()
        return Instant.parsePocketbase(dateStr)
    }

    override fun serialize(
        encoder: Encoder,
        value: Instant,
    ) {
        encoder.encodeString(value.toStringPocketbase())
    }
}
