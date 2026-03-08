package com.hulkdx.findprofessional.config.converter

import org.springframework.core.convert.TypeDescriptor
import org.springframework.core.convert.converter.GenericConverter
import org.springframework.data.convert.ReadingConverter
import java.time.Instant
import java.time.OffsetDateTime

@ReadingConverter
object OffsetDateTimeToInstantConverter : GenericConverter {

    private val convertibleTypes = setOf(
        GenericConverter.ConvertiblePair(OffsetDateTime::class.java, Instant::class.java)
    )

    override fun getConvertibleTypes(): Set<GenericConverter.ConvertiblePair> = convertibleTypes

    override fun convert(source: Any?, sourceType: TypeDescriptor, targetType: TypeDescriptor): Any? {
        return (source as? OffsetDateTime)?.toInstant()
    }
}
