package models

import io.github.agrevster.pocketbaseKotlin.models.utils.DurationPocketbase
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration

class DurationPocketbaseTest {
    @Test
    fun testSerializeDeserialize() {
        @Serializable
        data class TestSerializable(
            val duration: DurationPocketbase,
        )

        val original = TestSerializable(Duration.parse("5m"))
        assertEquals(5, original.duration.inWholeMinutes)

        val encoded = Json.encodeToString(original)
        assertEquals(expected = "{\"duration\":${5 * 60}}", actual = encoded)
        val decoded: TestSerializable = Json.decodeFromString(encoded)
        assertEquals(expected = original, actual = decoded)
    }
}
