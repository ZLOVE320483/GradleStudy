## Gradle Command

- 查看主要任务
```
    ./gradlew tasks
```

- 查看所有任务
```
    ./gradlew tasks --all
```

- 对某个module `[moduleName]` 的某个任务 `[TaskName]` 运行
```
    ./gradlew :moduleName:taskName
```

- 查看构建版本
```
    ./gradlew -v
```

- 清除 build 文件夹
```
    ./gradlew build
```

- 编译并安装 debug 包
```
    ./gradlew installDebug
```

- 编译并打印日志
```
    ./gradlew build --info
```

- 编译并输出性能报告，性能报告一般在构建工程根目录 build/reports/profile 下
```
    ./gradlew build --profile
```

- 调试模式构建并打印堆栈日志
```
    ./gradlew build --info --debug --stacktrace
```

- 强制更新最新依赖，清除构建后再构建
```
    ./gradlew clean build --refresh-dependencies
```

- 编译并打 Debug 包
```
    ./gradlew assembleDebug
    # 简化版命令，取各个单词的首字母
    ./gradlew aD
```

- 编译并打 Release 的包
```
    ./gradlew assembleRelease
    # 简化版命令，取各个单词的首字母
    ./gradlew aR
```

- Release 模式打包并安装
```
    ./gradlew installRelease
```

- 卸载 Release 模式包
```
    ./gradlew uninstallRelease
```

- debug release 模式全部渠道打包
```
    ./gradlew assemble
```

- 查看项目根目录下的依赖
```
    ./gradlew dependencies
```

- 查看 app 模块下的依赖
```
    ./gradlew app:dependencies
```

- 查看 app 模块下包含 implementation 关键字的依赖项目
```
    ./gradlew app:dependencies --configuration implementation
```