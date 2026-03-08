package com.hulkdx.findprofessional.config.converter

import org.springframework.core.convert.TypeDescriptor
import org.springframework.core.convert.converter.GenericConverter
import org.springframework.data.convert.WritingConverter
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

@WritingConverter
object InstantToOffsetDateTimeConverter : GenericConverter {

    private val convertibleTypes = setOf(
        GenericConverter.ConvertiblePair(Instant::class.java, OffsetDateTime::class.java)
    )

    override fun getConvertibleTypes(): Set<GenericConverter.ConvertiblePair> = convertibleTypes

    override fun convert(source: Any?, sourceType: TypeDescriptor, targetType: TypeDescriptor): Any? {
        return (source as? Instant)?.let { OffsetDateTime.ofInstant(it, ZoneOffset.UTC) }
    }
}
