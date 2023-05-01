import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val javaVersion = JavaVersion.VERSION_17

group = "com.hulkdx"
version = "1"
java.sourceCompatibility = javaVersion

plugins {
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.8.21"
    kotlin("plugin.spring") version "1.8.21"

    // enabled in prod.gradle:
    id("org.graalvm.buildtools.native") version "0.9.20" apply false
}

if (System.getenv("prod").toBoolean()) {
    apply(from = "prod.gradle")
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

    implementation("org.liquibase:liquibase-core")

    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.1.0")

    val mockitoKotlinVersion = "4.1.0"
    val testContainersVersion = "1.18.0"
    val coroutinesTestVersion = "1.6.4"

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.hamcrest:hamcrest-core")
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesTestVersion")

    integrationTestImplementation("org.springframework.boot:spring-boot-starter-test")
    integrationTestImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    integrationTestImplementation("org.testcontainers:postgresql:$testContainersVersion")
    integrationTestImplementation("org.testcontainers:r2dbc:$testContainersVersion")
    integrationTestImplementation("org.junit.jupiter:junit-jupiter")
    integrationTestImplementation("org.mockito:mockito-junit-jupiter")
    integrationTestImplementation("org.mockito:mockito-inline")
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
        dependsOn("integrationTest")

        testLogging {
            events(
                org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            )
        }
    }
}
