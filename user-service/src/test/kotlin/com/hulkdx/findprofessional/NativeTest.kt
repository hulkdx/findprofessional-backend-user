package com.hulkdx.findprofessional

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@ActiveProfiles("native-test")
@Testcontainers
class NativeTest {
    companion object {
        @Container
        private val container = PostgreSQLContainer("postgres:15-alpine")

        @DynamicPropertySource
        @JvmStatic
        private fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            if (!container.isCreated()) {
                container.start()
            }
            registry.add("spring.r2dbc.url") { container.jdbcUrl.replace("jdbc:", "r2dbc:") }
            registry.add("spring.r2dbc.username", container::getUsername)
            registry.add("spring.r2dbc.password", container::getPassword)
            registry.add("spring.liquibase.url", container::getJdbcUrl)
            registry.add("spring.liquibase.user", container::getUsername)
            registry.add("spring.liquibase.password", container::getPassword)
        }
    }

    @Test
    fun loadContext() {
    }
}
