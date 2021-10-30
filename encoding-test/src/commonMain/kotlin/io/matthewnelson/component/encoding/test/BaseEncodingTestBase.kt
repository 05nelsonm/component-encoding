package io.matthewnelson.component.encoding.test

import kotlin.test.assertEquals
import kotlin.test.assertNull

abstract class BaseEncodingTestBase {

    protected data class Data<Data: Any, Expected: Any?>(
        val raw: Data,
        val expected: Expected,
        val message: String? = null,
    )

    protected abstract val decodeSuccessHelloWorld: Data<String, ByteArray>

    protected abstract val decodeFailureDataSet: Set<Data<String, Any?>>
    protected abstract val decodeSuccessDataSet: Set<Data<String, ByteArray>>

    protected abstract val encodeSuccessDataSet: Set<Data<String, String>>

    protected abstract fun decode(data: String): ByteArray?
    protected abstract fun encode(data: ByteArray): String

    /**
     * Decoding failure should always result in `null`
     * */
    protected fun checkDecodeFailureForDataSet(
        dataSet: Set<Data<String, Any?>> = decodeFailureDataSet
    ) {
        check(dataSet.isNotEmpty()) { "dataSet cannot be empty" }

        for (data in dataSet) {
            val decoded = decode(data.raw)
            assertNull(decoded, data.message)
        }
    }

    protected fun checkDecodeSuccessForDataSet(
        dataSet: Set<Data<String, ByteArray>> = decodeSuccessDataSet
    ) {
        check(dataSet.isNotEmpty()) { "dataSet cannot be empty" }

        for (data in dataSet) {
            val decoded = decode(data.raw)

            assertEquals(
                expected = data.expected.size,
                actual = decoded?.size,
                message = "ByteArray size did not match after decoding $data"
            )

            for ((i, expectedByte) in data.expected.withIndex()) {
                assertEquals(
                    expected = expectedByte,
                    actual = decoded?.get(i),
                    message = "${data.message}. Byte[$i] did not match for $data"
                )
            }
        }
    }

    protected fun checkEncodeSuccessForDataSet(
        dataSet: Set<Data<String, String>> = encodeSuccessDataSet
    ) {
        check(dataSet.isNotEmpty()) { "dataSet cannot be empty" }

        for (data in dataSet) {
            val encoded = encode(data.raw.encodeToByteArray())
            assertEquals(data.expected, encoded, message = data.message)
        }
    }

    protected fun checkUniversalDecoderParameters() {
        assertNull(
            actual = decode(""),
            message = "Decoding an empty String should return null"
        )
        assertNull(
            actual = decode("      "),
            message = "Decoding a String with all spaces should return null"
        )

        // For Base32.Crockford, the standard '=' padding is only accepted when
        // appended as a check symbol, so checking here for all other decoders is ok
        assertNull(
            actual = decode("=="),
            message = "Decoding a String containing only padding '=' should return null"
        )

        val newHelloWorldEncodedString = decodeSuccessHelloWorld.raw.let { string ->
            val sb = StringBuilder()

            for ((i, c) in string.withIndex()) {
                sb.append(c)
                if (i % 2 == 0) {
                    sb.append("\n  ")
                } else if (i % 3 == 0) {
                    sb.append("\t \r  ")
                }
            }

            sb.append(" \n  ")
            sb.toString()
        }

        val decoded = decode(newHelloWorldEncodedString)

        assertEquals(
            expected = decodeSuccessHelloWorld.expected.size,
            actual = decoded?.size,
            message = "Decoded ByteArray sizes did not match when checking for chars '\\n', '\\t', '\\r', ' '"
        )

        for ((i, expectedByte) in decodeSuccessHelloWorld.expected.withIndex()) {
            assertEquals(
                expected = expectedByte,
                actual = decoded?.get(i),
                message = "Byte[$i] did not match for $decodeSuccessHelloWorld when checking for chars '\\n', '\\t', '\\r', ' '"
            )
        }
    }

    protected fun checkUniversalEncoderParameters() {
        assertEquals(
            expected = "",
            actual = encode(ByteArray(0)),
            message = "Encoding empty ByteArray should return an empty String"
        )
    }
}
