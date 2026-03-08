package com.hulkdx.findprofessional.config.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import java.time.Instant
import java.time.OffsetDateTime

@ReadingConverter
object OffsetDateTimeToInstantConverter : Converter<OffsetDateTime, Instant> {
    override fun convert(source: OffsetDateTime): Instant = source.toInstant()
}
