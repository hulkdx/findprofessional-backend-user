package com.hulkdx.findprofessional.config.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

@WritingConverter
object InstantToOffsetDateTimeConverter : Converter<Instant, OffsetDateTime> {
    override fun convert(source: Instant): OffsetDateTime = OffsetDateTime.ofInstant(source, ZoneOffset.UTC)
}
