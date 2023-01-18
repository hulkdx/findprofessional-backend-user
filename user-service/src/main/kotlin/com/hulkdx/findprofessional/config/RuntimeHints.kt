package com.hulkdx.findprofessional.config

import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.jdbc.datasource.SimpleDriverDataSource

class RuntimeHints : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        listOf(
            ArrayList::class.java,
            SimpleDriverDataSource::class.java,
        ).forEach {
            hints.reflection().registerType(it, *MemberCategory.values())
        }
        hints.resources().registerPattern("db/changelog/db.changelog-master.sql")
    }
}
