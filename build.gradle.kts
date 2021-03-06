import com.ofg.uptodate.UptodatePluginExtension
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.noarg.gradle.NoArgExtension

allprojects {

  group = "com.shardis"
  version = "0.1.5-SNAPSHOT"

  repositories {
    mavenCentral()
    maven { setUrl("https://repo.spring.io/milestone") }
  }

}

buildscript {
  val springBootVersion = "2.0.0.M2"

  project.extra.set("springBootVersion", springBootVersion)

  repositories {
    mavenCentral()
    maven { setUrl("https://repo.spring.io/milestone") }
  }

  dependencies {
    classpath("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
  }

}

plugins {
  val kotlinVersion = "1.1.3-2"
  val gradleNodePluginVersion = "1.2.0"
  val dependencyManagementVersion = "1.0.3.RELEASE"
  val uptodatePluginVersion = "1.6.3"
  val gradleDockerPluginVersion = "3.0.8"

  id("io.spring.dependency-management") version dependencyManagementVersion
  id("org.jetbrains.kotlin.jvm") version kotlinVersion
  id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
  id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
  id("org.jetbrains.kotlin.plugin.noarg") version kotlinVersion
  id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
  id("org.jetbrains.kotlin.kapt") version kotlinVersion
  id("com.moowork.node") version gradleNodePluginVersion
  id("com.bmuschko.docker-remote-api") version gradleDockerPluginVersion
  id("com.ofg.uptodate") version uptodatePluginVersion
}

subprojects {

  apply {
    plugin("idea")
    plugin("eclipse")
    plugin("java")
    plugin("jacoco")
    plugin("io.spring.dependency-management")
    plugin("org.jetbrains.kotlin.jvm")
    plugin("org.jetbrains.kotlin.kapt")
    plugin("org.jetbrains.kotlin.plugin.allopen")
    plugin("org.jetbrains.kotlin.plugin.spring")
    plugin("org.jetbrains.kotlin.plugin.noarg")
    plugin("org.jetbrains.kotlin.plugin.jpa")
    plugin("com.ofg.uptodate")
  }


  tasks.withType<KotlinCompile> {
    kotlinOptions {
      jvmTarget = "1.8"
      apiVersion = "1.1"
      languageVersion = "1.1"
      suppressWarnings = true
    }
  }

  configure<JavaPluginConvention> {
    setSourceCompatibility(1.8)
    setTargetCompatibility(1.8)
    sourceSets.getByName("main").java.srcDirs("${project.buildDir}/generated/source/kapt/main/")
  }

  configure<NoArgExtension> {
    annotation("org.axonframework.spring.stereotype.Aggregate")
  }


  configure<AllOpenExtension> {
    annotation("org.axonframework.spring.stereotype.Aggregate")
  }

  configure<UptodatePluginExtension> {
    reportProjectName = true
    setExcludedVersionPatterns(".*\\.pr\\d*$")
  }

  tasks.withType<JacocoReport> {
    reports.apply {
      html.isEnabled = true
      html.destination = file("${project.buildDir}/jacocoHtml")
      xml.isEnabled = true
    }
  }


  tasks.withType<Test> {
      testLogging.showStandardStreams = true
      testLogging.exceptionFormat = TestExceptionFormat.FULL
  }

  configure<DependencyManagementExtension> {
    imports {
      val springBootVersion = parent.extra.get("springBootVersion")
      mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
    }
  }

  dependencies {
    val jacksonVersion = the<DependencyManagementExtension>().importedProperties["jackson.version"]
    val querydslVersion = the<DependencyManagementExtension>().importedProperties["querydsl.version"]
    val axonVersion = "3.0.5"
    val jjwtVersion = "0.7.0"
    val reflectionsVersion = "0.9.11"

    configurations.compile.exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")

    compile("org.jetbrains.kotlin:kotlin-stdlib-jre8")
    compile("org.jetbrains.kotlin:kotlin-reflect:")

    compile("org.springframework.boot:spring-boot-devtools")
    compile("org.springframework.boot:spring-boot-starter-actuator")
    compile("org.springframework.boot:spring-boot-starter-cache")
    compile("org.springframework.boot:spring-boot-starter-data-jpa")
    compile("org.springframework.boot:spring-boot-starter-security")
    compile("org.springframework.boot:spring-boot-starter-undertow")
    compile("org.springframework.boot:spring-boot-starter-validation")
    compile("org.springframework.boot:spring-boot-starter-web")
    compile("org.springframework.boot:spring-boot-configuration-processor")

    compile("org.axonframework:axon-spring-boot-starter:$axonVersion")

    compile("org.hibernate:hibernate-java8")

    compile("com.google.guava:guava")

    compile("com.querydsl:querydsl-jpa:$querydslVersion")

    kapt("com.querydsl:querydsl-apt:$querydslVersion:jpa")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    kapt("org.hibernate:hibernate-jpamodelgen")

    compile("io.jsonwebtoken:jjwt:$jjwtVersion")

    compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    compile("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    runtime("com.h2database:h2")
    runtime("org.postgresql:postgresql")

    testCompile("org.jetbrains.kotlin:kotlin-test")
    testCompile("org.jetbrains.kotlin:kotlin-test-junit")
    testCompile("org.springframework.boot:spring-boot-starter-test")
    testCompile("org.springframework.security:spring-security-test")
    testCompile("org.reflections:reflections:$reflectionsVersion")
    testCompile("org.axonframework:axon-test:$axonVersion")
  }

  tasks.getByName("check").finalizedBy("jacocoTestReport")

}

