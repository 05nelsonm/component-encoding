rootProject.name = "component-encoding"

includeBuild("kotlin-components/includeBuild/dependencies")
includeBuild("kotlin-components/includeBuild/kmp")

// if JVM is not being built, don't include the app
@Suppress("PrivatePropertyName")
private val KMP_TARGETS: String? by settings

private val allTargets = System.getProperty("KMP_TARGETS_ALL") != null
private val targets = KMP_TARGETS?.split(',')

@Suppress("PrivatePropertyName")
private val CHECK_PUBLICATION: String? by settings
if (CHECK_PUBLICATION != null) {
    include(":tools:check-publication")
} else {
    include(":encoding-base16")
    include(":encoding-base32")
    include(":encoding-base64")
    include(":encoding-test")

    if (allTargets || targets?.contains("JVM") != false) {
        include(":app")
    }
}
