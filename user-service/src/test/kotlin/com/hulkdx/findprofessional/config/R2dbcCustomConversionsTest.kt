package com.hulkdx.findprofessional.config

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.springframework.core.convert.support.DefaultConversionService
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class R2dbcCustomConversionsTest {

    private val appConfiguration = AppConfiguration()

    @Test
    fun `registers instant and offset date time conversions`() {
        val conversions = appConfiguration.r2dbcCustomConversions()
        val conversionService = DefaultConversionService()
        conversions.registerConvertersIn(conversionService)
        val offsetDateTime = OffsetDateTime.ofInstant(Instant.parse("2026-03-08T12:34:56Z"), ZoneOffset.UTC)

        assertThat(conversions.hasCustomReadTarget(OffsetDateTime::class.java, Instant::class.java), `is`(true))
        assertThat(conversions.getCustomWriteTarget(Instant::class.java).orElse(null), notNullValue())
        assertThat(conversionService.convert(offsetDateTime, Instant::class.java), `is`(offsetDateTime.toInstant()))
        assertThat(
            conversionService.convert(offsetDateTime.toInstant(), OffsetDateTime::class.java),
            `is`(offsetDateTime)
        )
    }
}
