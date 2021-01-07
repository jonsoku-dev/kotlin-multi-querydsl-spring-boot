import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.1"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.4.30-M1" apply false
    id("org.jetbrains.kotlin.plugin.noarg") version "1.4.30-M1" apply false
    id("org.jetbrains.kotlin.kapt") version "1.4.30-M1" apply false
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.spring") version "1.4.21"
}

allprojects {
    repositories {
        // 요게 없으면 Cannot resolve external dependency org.jetbrains.kotlin:kotlin-compiler-embeddable:1.3.21 because no repositories are defined. 발생
        jcenter() // mavenCentral 인건 상관없네.
    }
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("kotlin-spring")
        plugin("kotlin-jpa")
        plugin("idea")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
        plugin( "org.jetbrains.kotlin.plugin.allopen")
        plugin( "org.jetbrains.kotlin.plugin.noarg")
        plugin("org.jetbrains.kotlin.kapt")
    }

    group = "com.tamastudy"
    version = "0.0.1-SNAPSHOT"
    java.sourceCompatibility = JavaVersion.VERSION_1_8

    /**
     * 서브모듈들에서 공통으로 사용될 dependency
     * 공통으로 사용될 것들은 여기에 정의를 해놓으면 프로젝트별로 따로 정해주지 않아도 된다.
     */
    dependencies {
        implementation("org.springframework.boot:spring-boot-configuration-processor")
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        compileOnly("org.springframework.boot:spring-boot-configuration-processor")
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
        annotationProcessor("org.projectlombok:lombok")
        developmentOnly("org.springframework.boot:spring-boot-devtools")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

project("common") {

    val jar: Jar by tasks
    val bootJar: org.springframework.boot.gradle.tasks.bundling.BootJar by tasks

    bootJar.enabled = false
    jar.enabled = true

    dependencies{
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        runtimeOnly("mysql:mysql-connector-java")
    }
}

project("api") {
    dependencies{
        api(project(":common"))
        implementation("org.springframework.boot:spring-boot-starter-security")
        implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity5")
        testImplementation("org.springframework.security:spring-security-test")
    }
}

allOpen {
    annotation("javax.persistence.Entity")
}