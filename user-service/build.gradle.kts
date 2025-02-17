import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

val javaVersion = JavaVersion.VERSION_17

group = "com.hulkdx"
version = "1"
java.sourceCompatibility = javaVersion

plugins {
    val kotlinVersion = "2.1.10"

    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    id("org.graalvm.buildtools.native") version "0.10.5"
}

tasks.processAot {
    isEnabled = System.getenv("prod").toBoolean()
}

tasks.processTestAot {
    isEnabled = System.getenv("prod").toBoolean()
}

graalvmNative {
    metadataRepository {
        enabled.set(true)
    }
    binaries {
        all {
            val nativeArgs = System.getenv("nativeArgs")
            if (nativeArgs != null) {
                buildArgs(nativeArgs)
            }
        }
    }
}

// region integrationTest

sourceSets {
    create("integrationTest") {
        compileClasspath += sourceSets.test.get().compileClasspath
        runtimeClasspath += sourceSets.test.get().runtimeClasspath
    }
}

val integrationTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

task<Test>("integrationTest") {
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath

    useJUnitPlatform()

    testLogging {
        events(
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
        )
    }
}

// endregion

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.postgresql:r2dbc-postgresql")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-jose")

    implementation("org.liquibase:liquibase-core:4.31.0")

    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.8.4")

    val mockitoKotlinVersion = "5.2.0"
    val testContainersVersion = "1.20.4"
    val coroutinesTestVersion = "1.10.1"

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.hamcrest:hamcrest-core")
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesTestVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")
    testImplementation("org.testcontainers:r2dbc:$testContainersVersion")
    testImplementation("com.fasterxml.jackson.core:jackson-annotations")
    testImplementation("com.fasterxml.jackson.core:jackson-databind")

    integrationTestImplementation(sourceSets["test"].output)
    integrationTestImplementation("org.springframework.boot:spring-boot-starter-test")
    integrationTestImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    integrationTestImplementation("org.testcontainers:postgresql:$testContainersVersion")
    integrationTestImplementation("org.testcontainers:r2dbc:$testContainersVersion")
    integrationTestImplementation("org.junit.jupiter:junit-jupiter")
    integrationTestImplementation("org.mockito:mockito-junit-jupiter")
    integrationTestImplementation("org.mockito:mockito-inline:$mockitoKotlinVersion")
    integrationTestImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    integrationTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = javaVersion.toString()
        }
    }

    test {
        useJUnitPlatform()

        testLogging {
            events(
                org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            )
        }
    }

    withType<BootBuildImage> {
        // Related to this issue: https://github.com/spring-projects/spring-boot/issues/36576
        environment.put("BP_NATIVE_IMAGE_BUILD_ARGUMENTS", "-march=compatibility")
    }
}
