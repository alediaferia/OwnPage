plugins {
	kotlin("jvm")
	kotlin("plugin.jpa") version kotlinVersion
	kotlin("plugin.allopen")
}

version = "0.0.1-SNAPSHOT"

val developmentOnly by configurations.creating
configurations {
	runtimeClasspath {
		extendsFrom(developmentOnly)
	}
}

allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.Embeddable")
	annotation("javax.persistence.MappedSuperclass")
	annotation("org.springframework.stereotype.Repository")
	annotation("org.springframework.stereotype.Service")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure:${Dependencies.Versions.springBootVersion}")
	implementation("org.springframework.security:spring-security-oauth2-resource-server")
	implementation("org.springframework.security:spring-security-oauth2-client")
	implementation("org.springframework.security:spring-security-oauth2-jose") // JWT support
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.postgresql:postgresql")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.1")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
	testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.2")
	testImplementation("org.apache.httpcomponents:httpclient:4.5.10")
	testImplementation("com.github.javafaker:javafaker:1.0.1")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
	testRuntimeOnly("com.h2database:h2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

