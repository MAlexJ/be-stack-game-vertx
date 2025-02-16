import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.be.stack.game"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.5.12"
val junitJupiterVersion = "5.9.1"
val resolverDnsVersion = "4.1.117.Final"

val mainVerticleName = "com.be.stack.game.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val slf4jApiVersion = "2.0.16"
val logbackClassicVersion = "1.5.16"
val javaDotenvVersion = "5.2.2"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

val jacksonDatabindVersion: String by project

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-web-validation")
  implementation("io.vertx:vertx-health-check")
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-json-schema")
  implementation("io.vertx:vertx-mongo-client")
  implementation("org.slf4j:slf4j-api:$slf4jApiVersion")
  implementation("ch.qos.logback:logback-classic:$logbackClassicVersion")
  implementation("io.vertx:vertx-health-check:$vertxVersion")
  implementation("io.github.cdimascio:java-dotenv:$javaDotenvVersion")
  implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonDatabindVersion")
  runtimeOnly("io.netty:netty-resolver-dns-native-macos:$resolverDnsVersion:osx-aarch_64")
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf(
    "run",
    mainVerticleName,
    "--redeploy=$watchForChange",
    "--launcher-class=$launcherClassName",
    "--on-redeploy=$doOnChange"
  )
}
