package com.hulkdx.findprofessional.utils

import com.github.dockerjava.api.model.RuntimeInfo
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar


class TestRuntimeHints : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        listOf(
            RuntimeInfo::class.java,
        ).forEach {
            hints.reflection().registerType(it, *MemberCategory.values())
        }
    }
}
