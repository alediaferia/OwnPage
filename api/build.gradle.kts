plugins {
	base
	kotlin("jvm") version kotlinVersion apply false
	id("org.springframework.boot") version Dependencies.Versions.springBootVersion
	id("io.spring.dependency-management") version "1.0.9.RELEASE" apply false
	kotlin("plugin.spring") version kotlinVersion apply false

	id("com.github.jk1.dependency-license-report") version "1.16"
}

allprojects {
	group = "com.alediaferia.ownpage"

	repositories {
		jcenter()
	}
}

subprojects {
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")
	apply(plugin = "org.jetbrains.kotlin.plugin.spring")
	apply(plugin = "java")

	the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
		imports {
			mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
		}
	}

	tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
		println("Configuring $name in project ${project.name}...")
		kotlinOptions {
			jvmTarget = "15"
//			freeCompilerArgs = listOf("-Xjsr305=strict")
		}
	}
}

dependencies {
	subprojects.forEach {
		archives(it)
	}
}

// Kotlin 1.4.30 IR support
// https://kotlinlang.org/docs/whatsnew1430.html#jvm-ir-compiler-backend-reaches-beta
tasks.withType(org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile::class) {
	kotlinOptions.useIR = true
}

