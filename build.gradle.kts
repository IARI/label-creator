import edu.sc.seis.launch4j.tasks.Launch4jExternalTask

val kotlinVersion = "1.3.50"
val mainClass = "com.julianjarecki.ettiketten.app.EttikettenApp"

plugins {
    id("com.github.johnrengelman.shadow") version "5.1.0"
    id("kotlinx-serialization") version "1.3.50"
    id("org.jetbrains.kotlin.jvm") version "1.3.50"

    application
    id("edu.sc.seis.launch4j") version "2.4.6"
}

//group 'com.julianjarecki.ettiketten'
//version '1.0-SNAPSHOT'
//sourceCompatibility = 1.8

repositories {
    mavenCentral()
}

dependencies {
    testCompile("io.kotlintest:kotlintest-runner-junit5:3.3.2")
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.10.0")

    compile("no.tornado:tornadofx:1.7.19")
    //compile 'org.controlsfx:controlsfx:9.0.0'
    //compile("org.controlsfx:controlsfx:8.40.15")

    //compile 'de.jensd:fontawesomefx:8.15.0'
    compile("de.jensd:fontawesomefx-commons:8.15")
    compile("de.jensd:fontawesomefx-controls:8.15")
    compile("de.jensd:fontawesomefx-fontawesome:4.7.0-5")
    compile("de.jensd:fontawesomefx-materialdesignfont:1.7.22-4")
    compile("de.jensd:fontawesomefx-materialicons:2.2.0-5")
    //compile("de.jensd:fontawesomefx-emojione:2.2.7-2")
    //compile("de.jensd:fontawesomefx-icons525:3.0.0-4")
    //compile("de.jensd:fontawesomefx-octicons:4.3.0-5")
    //compile("de.jensd:fontawesomefx-weathericons:2.0.10-5")

    //compile("org.apache.poi:poi:4.1.0")
    //compile("org.apache.poi:poi-ooxml:4.1.0")
    //compile("org.apache.xmlgraphics:batik-all:1.10")
    //compile("com.github.afester.javafx:FranzXaver:0.1")

    //compile("com.itextpdf:itextpdf:5.5.13.1")
    compile("com.itextpdf:kernel:7.1.8")
    //compile("com.itextpdf:io:7.1.8")
    compile("com.itextpdf:layout:7.1.8")
    //compile("com.itextpdf:forms:7.1.8")
    //compile("com.itextpdf:pdfs:7.1.8")

    // https://mvnrepository.com/artifact/net.java.dev.jna/jna
    //compile("net.java.dev.jna:jna:5.4.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs = listOf(
            "-Xuse-experimental=kotlin.Experimental",
            "-Xuse-experimental=kotlinx.serialization.ImplicitReflectionSerializer"
        )
    }

    application {
        mainClassName = mainClass
    }

    sourceSets {
        main {
            //kotlin.srcDirs += 'src/main/kotlin'
            //antlr.srcDirs += 'src/main/antlr'
            resources {
                srcDir("resources")
            }
        }
        test {

        }
        //    generated {
        //        java.srcDir 'generated-src/antlr/main'
        //    }
    }

    jar {
        manifest {
            attributes["Class-Path"] = configurations.compile.get().all.map { it.name }.joinToString(" ")
            attributes["Main-Class"] = mainClass
        }
    }



    launch4j {
        outfile = "Label Creator.exe"
        mainClassName = mainClass
        icon = "${projectDir}/resources/icons/label.ico"
        productName = "Label Creator"
        val shadowJar = project.tasks.shadowJar.get()
        copyConfigurable = shadowJar.outputs.files
        jar = "lib/${shadowJar.archiveFileName.get()}"
    }



    test {
        useJUnitPlatform()
    }
}