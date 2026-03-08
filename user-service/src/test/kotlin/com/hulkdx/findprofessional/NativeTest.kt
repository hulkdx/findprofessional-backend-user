package com.hulkdx.findprofessional

import com.hulkdx.findprofessional.model.User
import com.hulkdx.findprofessional.repository.UserRepository
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID

@SpringBootTest
@ActiveProfiles("native-test")
@Testcontainers
class NativeTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    companion object {
        @Container
        private val container = PostgreSQLContainer("postgres:17.9-alpine")

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

    @Test
    fun `can round trip user audit instants`() = runTest {
        val saved = userRepository.save(
            User(
                email = "native-${UUID.randomUUID()}@example.com",
                password = "password",
                firstName = "Native",
                lastName = "Image",
                profileImage = null,
                createdAt = null,
                updatedAt = null,
            )
        )

        val loaded = userRepository.findById(saved.id!!)

        assertThat(loaded, notNullValue())
        assertThat(loaded!!.email, `is`(saved.email))
        assertThat(loaded.createdAt, notNullValue())
        assertThat(loaded.updatedAt, notNullValue())
    }
}
