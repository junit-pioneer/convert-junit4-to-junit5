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

val junitMinorVersion = "7.0"
dependencies {
    implementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = "5.$junitMinorVersion")
    implementation(group = "org.junit.jupiter", name = "junit-jupiter-params", version = "5.$junitMinorVersion")
    implementation(group = "junit", name = "junit", version = "4.12")
    /*
    In versions after 3.8.3 has a bug if you try to preserve formatting
    https://github.com/javaparser/javaparser/pull/2027
    */
    implementation(group = "com.github.javaparser", name = "javaparser-core", version = "3.8.3")
    implementation(group = "org.hamcrest", name = "hamcrest-all", version = "1.3")
    implementation(group = "commons-io", name = "commons-io", version = "2.5")

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = "5.$junitMinorVersion")
    testImplementation(group = "org.junit.platform", name = "junit-platform-launcher", version = "1.$junitMinorVersion")
    testImplementation(group = "org.junit.platform", name = "junit-platform-testkit", version = "1.$junitMinorVersion")

    testImplementation(group = "org.assertj", name = "assertj-core", version = "3.15.0")
    testImplementation(group = "org.mockito", name = "mockito-core", version = "3.3.3")

    testRuntimeOnly(group = "org.apache.logging.log4j", name = "log4j-core", version = "2.12.1")
    testRuntimeOnly(group = "org.apache.logging.log4j", name = "log4j-jul", version = "2.12.1")
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
}
