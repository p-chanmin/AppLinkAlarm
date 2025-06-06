import com.dev.configureHiltAndroid
import com.dev.configureKotestAndroid
import com.dev.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
            }

            configureKotlinAndroid()
            configureHiltAndroid()
            configureKotestAndroid()
        }
    }
}