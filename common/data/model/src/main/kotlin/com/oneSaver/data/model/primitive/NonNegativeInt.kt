package com.oneSaver.data.model.primitive

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.oneSaver.data.model.exact.Exact

@JvmInline
value class NonNegativeInt private constructor(val value: Int) {
    companion object : Exact<Int, NonNegativeInt> {
        override val exactName = "NonNegativeInt"

        override fun Raise<String>.spec(raw: Int): NonNegativeInt {
            ensure(raw >= 0) { "$raw is not >= 0" }
            return NonNegativeInt(raw)
        }

        val Zero = NonNegativeInt.unsafe(0)
    }
}

fun PositiveInt.toNonNegative(): NonNegativeInt = NonNegativeInt.unsafe(value)
