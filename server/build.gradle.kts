import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion:    String by project
val kotlinVersion:  String by project
val logbackVersion: String by project
val kMongoVersion:  String by project
val koinVersion:    String by project
val jedisVersion:   String by project
val exposedVersion: String by project

plugins {
    application
    id("io.ktor.plugin")
    kotlin("plugin.serialization")
    kotlin("jvm")
}

group = "com.practise"
version = "1.0.0"
application {
    mainClass.set("io.ktor.server.cio.EngineMain")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

tasks {
    create("stage").dependsOn("installDist")
    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "19"
            freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-cio-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-freemarker:$ktorVersion")
    implementation("io.ktor:ktor-server-swagger:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-http-redirect-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-freemarker-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")


    //Ktor for client
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-auth:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:$ktorVersion")

    //KMongo: MongoDB client
    implementation("org.litote.kmongo:kmongo-async:$kMongoVersion")
    implementation("org.litote.kmongo:kmongo-id-serialization:$kMongoVersion")
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:$kMongoVersion")

    //Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    //Jedis: Java Redis client
    implementation("redis.clients:jedis:$jedisVersion")


    //Koin
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

    //Google Client Api
    implementation("com.google.api-client:google-api-client:2.1.2")
}