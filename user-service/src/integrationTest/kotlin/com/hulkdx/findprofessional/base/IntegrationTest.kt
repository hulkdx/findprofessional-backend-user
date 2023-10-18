package com.hulkdx.findprofessional.base

import com.fasterxml.jackson.core.io.SerializedString
import com.fasterxml.jackson.databind.ext.Java7HandlersImpl
import com.github.dockerjava.api.model.AuthConfig
import org.springframework.aot.hint.ExecutableMode
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.BootstrapUtils
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.aot.TestRuntimeHintsRegistrar
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.shaded.com.github.dockerjava.core.DockerConfigFile

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class IntegrationTest {
    companion object {
        @Container
        private val container = PostgreSQLContainer("postgres:15-alpine")

        @DynamicPropertySource
        @JvmStatic
        private fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") { container.jdbcUrl.replace("jdbc:", "r2dbc:") }
            registry.add("spring.r2dbc.username", container::getUsername)
            registry.add("spring.r2dbc.password", container::getPassword)
            registry.add("spring.liquibase.url", container::getJdbcUrl)
            registry.add("spring.liquibase.user", container::getUsername)
            registry.add("spring.liquibase.password", container::getPassword)
        }
    }
}

class TestRuntimeHints: TestRuntimeHintsRegistrar {
    override fun registerHints(
        hints: RuntimeHints,
        testClass: Class<*>,
        classLoader: ClassLoader,
    ) {
        println("REGISTER HINTS: Test")

        listOf(
            BootstrapUtils::class.java,
            AuthConfig::class.java,
            java.nio.file.Path::class.java,
            Map::class.java,
            HashMap::class.java,
            Java7HandlersImpl::class.java,
        ).forEach {
            hints.reflection().registerType(it, *MemberCategory.values())
        }
        hints.serialization().registerType(AuthConfig::class.java)
        hints.serialization().registerType(HashMap::class.java)
        hints.serialization().registerType(SerializedString::class.java)

        hints.reflection().registerType(
            DockerConfigFile::class.java,
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
            MemberCategory.INVOKE_DECLARED_METHODS,
            MemberCategory.DECLARED_FIELDS,
        )

        DockerConfigFile::class.java.declaredConstructors.forEach {
            hints.reflection().registerConstructor(it, ExecutableMode.INVOKE)
        }

        hints.resources().registerPattern("/home/runner/.docker/config.json")
    }

}

class TestHints : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        println("REGISTER HINTS")
        listOf(
            BootstrapUtils::class.java,
            AuthConfig::class.java,
            java.nio.file.Path::class.java,
            Map::class.java,
            HashMap::class.java,
            Java7HandlersImpl::class.java,
        ).forEach {
            hints.reflection().registerType(it, *MemberCategory.values())
        }
        hints.serialization().registerType(AuthConfig::class.java)
        hints.serialization().registerType(HashMap::class.java)
        hints.serialization().registerType(SerializedString::class.java)

        hints.reflection().registerType(
            DockerConfigFile::class.java,
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
            MemberCategory.INVOKE_DECLARED_METHODS,
            MemberCategory.DECLARED_FIELDS,
        )

        DockerConfigFile::class.java.declaredConstructors.forEach {
            hints.reflection().registerConstructor(it, ExecutableMode.INVOKE)
        }

        hints.resources().registerPattern("/home/runner/.docker/config.json")
    }
}
