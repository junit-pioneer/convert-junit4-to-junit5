plugins {
    java
}

group = "org.junit-pioneer"
description = "Convert junit 4 to junit 5 test"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

val junitVersion = "5.9.1"
dependencies {
    implementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = junitVersion)
    implementation(group = "org.junit.jupiter", name = "junit-jupiter-params", version = junitVersion)
    implementation(group = "junit", name = "junit", version = "4.13.2")
    implementation(group = "com.github.javaparser", name = "javaparser-core", version = "3.25.2")
    implementation(group = "org.hamcrest", name = "hamcrest-all", version = "1.3")
    implementation(group = "commons-io", name = "commons-io", version = "2.12.0")
    implementation(group = "com.beust", name = "jcommander", version = "1.82")

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = junitVersion)
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
    compileTestJava {
        options.encoding = "UTF-8"
    }
    test {
        useJUnitPlatform()
        filter {
            includeTestsMatching("*Test")
        }
        testLogging {
            setExceptionFormat("full")
        }
    }
    register<Jar>("fatJar") {
        group = "application"
        manifest {
            attributes["Implementation-Version"] = archiveVersion
            attributes["Main-Class"] = "jb.CommandLineRunner"
        }
        archiveBaseName.set("${project.name}-fat")
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        with(jar.get() as CopySpec)
    }
    "build" {
        dependsOn("fatJar")
    }
}

