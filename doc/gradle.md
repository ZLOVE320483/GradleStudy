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

this.gradle.settingsEvaluated { Settings settings ->
    println("single settingsEvaluated setting.gradle初始化完成 root project name  = " + settings.rootProject.name)
}

this.gradle.projectsLoaded {
    println("single projectsLoaded 完成项目构建初始化阶段")
}

this.gradle.allprojects {
    println("all projects, the project name = ${it.name}")
    it.beforeEvaluate {
        println(String.format("%s , before Evaluate", it.name))
    }
    it.afterEvaluate {
        println(String.format("%s , after Evaluate", it.name))
    }
}

this.gradle.beforeProject {
    println(String.format("%s , before project ", it.name))
}

this.gradle.afterProject {
    println(String.format("%s , after project ", it.name))
}

//执行阶段生命周期监听
this.gradle.taskGraph.whenReady {
    gradle.taskGraph.allTasks.each {
        println("Task each  = " + it.name)
    }
}

this.gradle.taskGraph.beforeTask {
    println("Task before  = " + it.name)
}

this.gradle.taskGraph.afterTask {
    println("Task after  = " + it.name)
}
 ```
运行结果：
```
settingsEvaluated, setting.gradle初始化完成, root project name  = GradleStudy
single settingsEvaluated setting.gradle初始化完成 root project name  = GradleStudy
projectsLoaded, project 完成项目构建初始化阶段...
all projects, the project name = GradleStudy
all projects, the project name = app
single projectsLoaded 完成项目构建初始化阶段

> Configure project :
GradleStudy , before project 
GradleStudy , before Evaluate
GradleStudy , after project 
GradleStudy , after Evaluate

> Configure project :app
app , before project 
app , before Evaluate
app , after project 
app , after Evaluate
projectsEvaluated, 完成项目的配置阶段...
Task each  = prepareKotlinBuildScriptModel

> Task :prepareKotlinBuildScriptModel UP-TO-DATE
Task before  = prepareKotlinBuildScriptModel
Task after  = prepareKotlinBuildScriptModel
buildFinished, 完成项目的编译...
```


