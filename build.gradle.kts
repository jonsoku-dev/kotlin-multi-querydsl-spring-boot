import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.4.21"
    id("org.springframework.boot") version "2.4.1"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.kapt") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion apply false
    id("org.jetbrains.kotlin.plugin.noarg") version kotlinVersion apply false
    id("com.ewerk.gradle.plugins.querydsl") version "1.0.10" apply false
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion

    idea
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
        plugin("kotlin-kapt")
        plugin("kotlin-spring")
        plugin("kotlin-jpa")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
        plugin("org.jetbrains.kotlin.plugin.allopen")
        plugin("org.jetbrains.kotlin.plugin.noarg")
    }

    group = "com.tamastudy"
    version = "0.0.1-SNAPSHOT"
    java.sourceCompatibility = JavaVersion.VERSION_1_8

    /**
     * 서브모듈들에서 공통으로 사용될 dependency
     * 공통으로 사용될 것들은 여기에 정의를 해놓으면 프로젝트별로 따로 정해주지 않아도 된다.
     */
    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21")
        implementation("org.springframework.boot:spring-boot-configuration-processor")
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.5.8")
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
//    sourceSets["main"].withConvention(KotlinSourceSet::class) {
//        kotlin.srcDir("build/generated/source/kapt/main")
//        println(kotlin.srcDirs)
//    }

    apply {
        plugin("com.ewerk.gradle.plugins.querydsl")
        plugin("idea")
    }

    dependencies{
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        runtimeOnly("mysql:mysql-connector-java")

        compileOnly("com.querydsl:querydsl-jpa:4.2.1")
        kapt("com.querydsl:querydsl-apt:4.2.1:jpa")
    }

    val jar: Jar by tasks
    val bootJar: org.springframework.boot.gradle.tasks.bundling.BootJar by tasks

    bootJar.enabled = false
    jar.enabled = true

    idea {
        module {
            val kaptMain = file("build/generated/source/kapt/main")
            sourceDirs.add(kaptMain)
            generatedSourceDirs.add(kaptMain)
        }
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