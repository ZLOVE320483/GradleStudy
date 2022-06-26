## Gradle Study

### gradle 声明周期监听
- lifecycle_listener.gradle
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
<=== gradle lifecycle listener ===> settingsEvaluated, setting.gradle初始化完成, root project name  = GradleStudy
<=== gradle lifecycle listener ===> single settingsEvaluated setting.gradle初始化完成 root project name  = GradleStudy
<=== gradle lifecycle listener ===> projectsLoaded, project 完成项目构建初始化阶段...
<=== gradle lifecycle listener ===> all projects, the project name = GradleStudy
<=== gradle lifecycle listener ===> all projects, the project name = app
<=== gradle lifecycle listener ===> single projectsLoaded 完成项目构建初始化阶段

> Configure project :
<=== gradle lifecycle listener ===> GradleStudy , before project
<=== gradle lifecycle listener ===> GradleStudy , before Evaluate
<=== gradle lifecycle listener ===> GradleStudy , after project
<=== gradle lifecycle listener ===> GradleStudy , after Evaluate

> Configure project :app
<=== gradle lifecycle listener ===> app , before project
<=== gradle lifecycle listener ===> app , before Evaluate
<=== gradle lifecycle listener ===> app , after project
<=== gradle lifecycle listener ===> app , after Evaluate
<=== gradle lifecycle listener ===> projectsEvaluated, 完成项目的配置阶段...
<=== gradle lifecycle listener ===> Task each  = prepareKotlinBuildScriptModel

> Task :prepareKotlinBuildScriptModel UP-TO-DATE
<=== gradle lifecycle listener ===> Task before  = prepareKotlinBuildScriptModel
<=== gradle lifecycle listener ===> Task after  = prepareKotlinBuildScriptModel
<=== gradle lifecycle listener ===> buildFinished, 完成项目的编译...
```


