plugins {
    java
    `maven-publish`
}

group = "net.runelite"
version = "1.0"

repositories {
    mavenCentral()
}

val lombokVersion = libs.versions.lombok.get()

dependencies {
    implementation("org.ow2.asm:asm:9.2")
    implementation("org.ow2.asm:asm-util:9.2")
    implementation("org.ow2.asm:asm-commons:9.2")

    // implementation("net.runelite:client:1.10.38-SNAPSHOT")

    implementation(libs.jetbrains.annotations)
    implementation(libs.guava)
    implementation(libs.slf4j.api)
    implementation(libs.slf4j.simple)

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

// Create jar with dependencies
tasks.register<Jar>("fatJar") {
    archiveClassifier.set("all")
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)

    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    }) {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    manifest {
        attributes(
            "Implementation-Title" to "RuneLite Agent",
            "Implementation-Version" to version,
            "Manifest-Version" to "1.0",
            "Premain-Class" to "net.runelite.Agent",
            "Agent-Class" to "net.runelite.Agent",
            "Can-Redefine-Classes" to "true",
            "Can-Retransform-Classes" to "true",
            "Created-By" to "Melxin"
        )
    }
}

tasks.named<Jar>("fatJar") {
    mustRunAfter(tasks.named("classes"))
}

tasks.named("build") {
    dependsOn("fatJar")
}