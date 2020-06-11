val kotlinVersion = "1.3.72"
val appMainClassName = "com.julianjarecki.ettiketten.app.EttikettenApp"
version = "0.0.1"

plugins {
    id("com.github.johnrengelman.shadow") version "5.1.0"
    id("kotlinx-serialization") version "1.3.72"
    kotlin("jvm") version "1.3.72"

    application
    id("edu.sc.seis.launch4j") version "2.4.6"

    id("com.github.breadmoirai.github-release") version "2.2.9"
}

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/iari/maven")
}

dependencies {
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")

    implementation("no.tornado:tornadofx:1.7.20")
    //implementation("org.controlsfx:controlsfx:9.0.0")
    //implementation("org.controlsfx:controlsfx:8.40.15")

    // use tfx serializer to serialize javafx prooperties
    implementation("com.julianjarecki:TFXserializer:1.1.0")

    //compile 'de.jensd:fontawesomefx:8.15.0'
    implementation("de.jensd:fontawesomefx-commons:8.15")
    implementation("de.jensd:fontawesomefx-controls:8.15")
    implementation("de.jensd:fontawesomefx-fontawesome:4.7.0-5")
    implementation("de.jensd:fontawesomefx-materialdesignfont:1.7.22-4")
    implementation("de.jensd:fontawesomefx-materialicons:2.2.0-5")
    //implementation("de.jensd:fontawesomefx-emojione:2.2.7-2")
    //implementation("de.jensd:fontawesomefx-icons525:3.0.0-4")
    //implementation("de.jensd:fontawesomefx-octicons:4.3.0-5")
    //implementation("de.jensd:fontawesomefx-weathericons:2.0.10-5")

    //implementation("org.apache.poi:poi:4.1.0")
    //implementation("org.apache.poi:poi-ooxml:4.1.0")
    //implementation("org.apache.xmlgraphics:batik-all:1.10")
    //implementation("com.github.afester.javafx:FranzXaver:0.1")

    //implementation("com.itextpdf:itextpdf:5.5.13.1")
    implementation("com.itextpdf:kernel:7.1.11")
    //implementation("com.itextpdf:io:7.1.11")
    implementation("com.itextpdf:layout:7.1.11")
    //implementation("com.itextpdf:forms:7.1.11")
    //implementation("com.itextpdf:pdfs:7.1.11")

    // https://mvnrepository.com/artifact/net.java.dev.jna/jna
    //implementation("net.java.dev.jna:jna:5.4.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs = listOf(
            "-Xuse-experimental=kotlin.Experimental",
            "-Xopt-in=kotlin.ExperimentalStdlibApi",
            "-Xuse-experimental=kotlinx.serialization.ImplicitReflectionSerializer"
        )
    }

    application {
        mainClassName = appMainClassName
    }

    sourceSets {
        main {
            resources {
                srcDir("resources")
            }
        }
        test {
        }
    }

    jar {
        manifest {
            attributes["Class-Path"] = configurations.compile.get().all.map { it.name }.joinToString(" ")
            attributes["Main-Class"] = appMainClassName
        }
    }

    launch4j {
        outfile = "Label Creator.exe"
        mainClassName = appMainClassName
        icon = "${projectDir}/resources/icons/label.ico"
        productName = "Label Creator"
        val shadowJar = project.tasks.shadowJar.get()
        copyConfigurable = shadowJar.outputs.files
        jar = "lib/${shadowJar.archiveFileName.get()}"
    }

    test {
        useJUnitPlatform()
    }

    githubRelease {
        repo("label-creator")
        owner("IARI")
        //targetCommitish("master")
        overwrite(true)
        draft(true)
    }
}