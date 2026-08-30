@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.loom)
    alias(libs.plugins.ksp)
    alias(libs.plugins.fletchingTable)
}

val ver = stonecutter.current.version
val modId = project.property("mod.id").toString()
val modName = project.property("mod.name").toString()
val modVer = project.property("mod.version").toString()

version = "$modVer+$ver"
base.archivesName = modId

repositories {
    fun strictMaven(url: String, vararg groups: String) = maven(url) { content { groups.forEach(::includeGroupAndSubgroups) } }

    strictMaven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1", "me.djtheredstoner")
    strictMaven("https://api.modrinth.com/maven", "maven.modrinth")
    strictMaven("https://maven.parchmentmc.org/", "org.parchmentmc")
    strictMaven("https://jitpack.io", "com.github.stivais", "com.github.odtheking", "com.github.sivthepolarfox", "com.github.skies-starred")

    maven("https://maven.starred.foo/releases")
    maven("https://maven.starred.foo/snapshots")
}

fletchingTable {
    mixins.create("main", Action {
        mixin("default", "$modId.mixins.json") {
            env("CLIENT")
        }
    })
}

dependencies {
    minecraft("com.mojang:minecraft:$ver")

    localRuntime("devauth".global)
    compileOnly("firmament".versioned)

    implementation("fabric-api".versioned)
    implementation("fabric-loader".global)
    implementation("fabric-language-kotlin".global)

    implementation("odin-prod".versioned)
    implementation("commodore".global)
    implementation("lwjgl-nanovg".global)

    shadow("snowbird".versioned)
    shadow("kommand".global)
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")

    runConfigs.named("client") {
        isIdeConfigGenerated = true
        vmArgs.addAll(
            arrayOf(
                "-Ddevauth.enabled=true",
                "-Ddevauth.account=main",
                "-XX:+AllowEnhancedClassRedefinition"
            )
        )
    }

    runConfigs.named("server") {
        isIdeConfigGenerated = false
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(25)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
    withSourcesJar()
}

kotlin {
    jvmToolchain(25)

    compilerOptions {
        jvmTarget.set(JvmTarget.valueOf("JVM_25"))

        freeCompilerArgs.addAll("-XXLanguage:+ExplicitBackingFields", "-Xcontext-parameters", "-Xcontext-sensitive-resolution", "-Xlambdas=class")
        optIn.add("kotlin.time.ExperimentalTime")
    }
}

tasks {
    processResources {
        val r = mapOf(
            "id" to modId,
            "name" to modName,
            "version" to modVer,
            "minecraft" to project.property("mod.mc_dep")
        )

        inputs.properties(r)
        filesMatching("fabric.mod.json") { expand(r) }
    }

    register<Copy>("buildAndCollect") {
        description = "Builds and collects mod jars."
        group = "build"
        from(jar, kotlinSourcesJar)
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}

val String.global: Provider<MinimalExternalModuleDependency>
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs").findLibrary(this).get()

val String.versioned: Provider<MinimalExternalModuleDependency>
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs").findLibrary("$this-${ver.replace(".", "_")}").get()

fun DependencyHandlerScope.shadow(dep: Any, config: ExternalModuleDependency.() -> Unit = {}) {
    val d = create((dep as? Provider<*>)?.get() ?: dep) as ExternalModuleDependency
    d.config()
    include(d)
    implementation(d)
}
