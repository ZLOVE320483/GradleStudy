## Gradle Study

### gradle 声明周期监听
- settings.gradle
```groovy
this.gradle.addBuildListener(new BuildListener() {

    @Override
    void settingsEvaluated(Settings settings) {
        println("settingsEvaluated, setting.gradle初始化完成, root project name  = ${settings.rootProject.name}")
    }

    @Override
    void projectsLoaded(Gradle gradle) {
        println("projectsLoaded, project 完成项目构建初始化阶段...")
    }

    @Override
    void projectsEvaluated(Gradle gradle) {
        println("projectsEvaluated, 完成项目的配置阶段...")
    }

    @Override
    void buildFinished(BuildResult buildResult) {
        println("buildFinished, 完成项目的编译...")
    }
})
 ```
运行结果：
```
settingsEvaluated, setting.gradle初始化完成, root project name  = GradleStudy
projectsLoaded, project 完成项目构建初始化阶段...
projectsEvaluated, 完成项目的配置阶段...
> Task :prepareKotlinBuildScriptModel UP-TO-DATE
buildFinished, 完成项目的编译...

```
