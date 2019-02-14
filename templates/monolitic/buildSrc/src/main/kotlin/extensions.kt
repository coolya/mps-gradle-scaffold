import org.gradle.api.*

fun Project.getJdkHome(): String {
    // Detect jdk location, required to start ant with tools.jar on classpath otherwise javac and tests will fail
    val javaHome = System.getProperty("java.home")
    val jdkHome = if (!file("$javaHome/lib/tools.jar").isFile()) {
        javaHome + "/.."
    } else {
        javaHome
    }
    if (!file("$jdkHome/lib/tools.jar").isFile()) {
        throw GradleException("Was not able to locate jdk home folder. Use 'jdk_home' project variable to specify JDK location explicitly. Current JAVA_HOME is: $javaHome")
    }
    return jdkHome
}
