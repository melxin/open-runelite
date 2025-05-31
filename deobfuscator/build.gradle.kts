/*
 * Copyright (c) 2025, Melxin <https://github.com/melxin>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
plugins {
    java
    `maven-publish`
}

group = "net.runelite"
//version = "1.0"
//val rlVersion = project.version //"1.13.7-SNAPSHOT"
val rlVersion = providers.gradleProperty("project.build.version").getOrElse(project.version.toString())

repositories {
    mavenCentral()

    maven {
        name = "RuneLite"
        url = uri("https://repo.runelite.net")
    }
}

val injectedClient by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
    isTransitive = false
}

val lombokVersion = libs.versions.lombok.get()

dependencies {
    implementation("org.ow2.asm:asm:9.2")
    implementation("org.ow2.asm:asm-util:9.2")
    implementation("org.ow2.asm:asm-commons:9.2")

    implementation(libs.jetbrains.annotations)
    implementation(libs.guava)
    implementation(libs.slf4j.api)
    implementation(libs.logback.core)
    implementation(libs.logback.classic) {
        exclude("org.slf4j", "slf4j-api")
    }
    //runtimeOnly("org.slf4j:slf4j-simple:1.7.25")

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    compileOnly(libs.findbugs)
    implementation(libs.jopt)

    injectedClient("net.runelite:injected-client:$rlVersion")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.register<JavaExec>("deob") {
    //dependsOn(tasks.classes)

    group = "transformation"
    description = "Transforms RuneLite's injected-client."

    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("net.melxin.asm.transformer.Main")
    jvmArgs("-Xmx1G")

    val inputJar = injectedClient.elements.map { elements ->
        elements.single { element ->
            element.asFile.name.startsWith("injected-client-$rlVersion") &&
                    element.asFile.extension.equals("jar", ignoreCase = true)
        }.asFile
    }

    val outputJar = layout.buildDirectory.file("deob/injected-client-$rlVersion.jar")

    inputs.file(inputJar)
            .withPropertyName("inputJar")
            .withPathSensitivity(PathSensitivity.ABSOLUTE)

    outputs.file(outputJar)
            .withPropertyName("outputJar")

    doFirst {
        outputJar.get().asFile.parentFile.mkdirs()
    }

    argumentProviders.add {
        listOf(
                "--input", inputJar.get().absolutePath,
                "--output", outputJar.get().asFile.absolutePath,
                "--minimal"
        )
    }
}

/*tasks.named("build") {
    dependsOn("deob")
}*/