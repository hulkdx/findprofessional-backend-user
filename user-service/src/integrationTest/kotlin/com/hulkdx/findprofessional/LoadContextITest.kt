package com.hulkdx.findprofessional

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledInNativeImage
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@EnabledInNativeImage
class LoadContextITest {

    @Test
    fun loadContext() {
    }
}
