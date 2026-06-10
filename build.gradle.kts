plugins {
    id("java")
    id("application")
}

group = "org.school21"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.googlecode.lanterna:lanterna:3.1.2")
}

java{
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

application {
    mainClass.set("org.rogue.app.Main")
}

tasks.test {
    useJUnitPlatform()
}