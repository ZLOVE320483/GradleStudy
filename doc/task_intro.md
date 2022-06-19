## 打包过程主要Task简介

**APK打包八大流程**

1、首先，.aidl（Android Interface Description Language）文件需要通过 aidl 工具转换成编译器能够处理的 Java 接口文件。

2、同时，资源文件（包括 AndroidManifest.xml、布局文件、各种 xml 资源等等）将被 AAPT（Asset Packaging Tool）（Android Gradle Plugin 3.0.0 及之后使用 AAPT2 替代了 AAPT）处理为最终的 resources.arsc，并生成 R.java 文件以保证源码编写时可以方便地访问到这些资源。

3、然后，通过 Java Compiler 编译 R.java、Java 接口文件、Java 源文件，最终它们会统一被编译成 .class 文件。

4、因为 .class 并不是 Android 系统所能识别的格式，所以还需要通过 dex 工具将它们转化为相应的 Dalvik 字节码（包含压缩常量池以及清除冗余信息等工作）。这个过程中还会加入应用所依赖的所有 “第三方库”。

5、下一步，通过 ApkBuilder 工具将资源文件、DEX 文件打包生成 APK 文件。

6、接着，系统将上面生成的 DEX、资源包以及其它资源通过 apkbuilder 生成初始的 APK 文件包。

7、然后，通过签名工具 Jarsigner 或者其它签名工具对 APK 进行签名得到签名后的 APK。如果是在 Debug 模式下，签名所用的 keystore 是系统自带的默认值，否则我们需要提供自己的私钥以完成签名过程。

8、最后，如果是正式版的 APK，还会利用 ZipAlign 工具进行对齐处理，以提高程序的加载和运行速度。而对齐的过程就是将 APK 文件中所有的资源文件距离文件的起始位置都偏移4字节的整数倍，这样通过 mmap 访问 APK 文件的速度会更快，并且会减少其在设备上运行时的内存占用。

### 主要Task解析

运行命令 `./gradlew app:assembleDebug --console=plain`

```
> Task :app:preBuild UP-TO-DATE
> Task :app:preDebugBuild UP-TO-DATE
> Task :app:generateDebugBuildConfig
> Task :app:javaPreCompileDebug
> Task :app:mainApkListPersistenceDebug
> Task :app:generateDebugResValues
> Task :app:createDebugCompatibleScreenManifests
> Task :app:extractDeepLinksDebug
> Task :app:compileDebugAidl NO-SOURCE
> Task :app:compileDebugRenderscript NO-SOURCE
> Task :app:generateDebugResources
> Task :app:processDebugManifest
> Task :app:mergeDebugResources
> Task :app:processDebugResources
> Task :app:compileDebugJavaWithJavac
> Task :app:compileDebugSources
> Task :app:mergeDebugShaders
> Task :app:compileDebugShaders
> Task :app:generateDebugAssets
> Task :app:mergeDebugAssets
> Task :app:processDebugJavaRes NO-SOURCE
> Task :app:checkDebugDuplicateClasses
> Task :app:dexBuilderDebug
> Task :app:mergeLibDexDebug
> Task :app:mergeDebugJavaResource
> Task :app:mergeDebugJniLibFolders
> Task :app:validateSigningDebug
> Task :app:mergeProjectDexDebug
> Task :app:mergeDebugNativeLibs
> Task :app:stripDebugDebugSymbols
> Task :app:desugarDebugFileDependencies
> Task :app:mergeExtDexDebug
> Task :app:packageDebug
> Task :app:assembleDebug
```

| Task | 对应实现类 | 作用 |
| :---- | :---- | :---- |
| preBuild | AppPreBuildTask | 预先创建的 task，用于做一些 application Variant 的检查 |
| preDebugBuild |  | 与 preBuild 区别是这个 task 是用于在 Debug 的环境下的一些 Vrariant 检查 |
| generateDebugBuildConfig | GenerateBuildConfig | 生成与构建目标相关的 BuildConfig 类|
| javaPreCompileDebug | JavaPreCompileTask | 用于在 Java 编译之前执行必要的 action |
| mainApkListPersistenceDebug | MainApkListPersistence	| 用于持久化 APK 数据 |
| generateDebugResValues | GenerateResValues | 生成 Res 资源类型值 |
| createDebugCompatibleScreenManifests | CompatibleScreensManifest | 生成具有给定屏幕密度与尺寸列表的 （兼容屏幕）节点清单 |
| extractDeepLinksDebug | ExtractDeepLinksTask | 用于抽取一系列 DeepLink（深度链接技术，主要应用场景是通过Web页面直接调用Android原生app，并且把需要的参数通过Uri的形式，直接传递给app，节省用户的注册成本） |
| compileDebugAidl | AidlCompile | 编译 AIDL 文件 |