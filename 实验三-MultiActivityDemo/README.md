# 实验三 多 Activity（MultiActivityDemo）

Android 移动应用开发 实验三：1 个 MainActivity + 4 个二级 Activity。
应用标题：**兰天浩2024110515**。

## 一、环境要求

| 项目 | 版本 |
| --- | --- |
| Android Studio | Hedgehog 及以上（支持 AGP 7.4.2） |
| Gradle | 7.6（已带 wrapper） |
| JDK | 17 |
| 编译 SDK | Android 13 / API 33 |
| 最低运行版本 | Android 7.0 / API 24 |
| 开发语言 | Java |

## 二、运行步骤

1. 用 Android Studio 打开 `MultiActivityDemo` 目录，等待 Gradle 同步。
2. 若提示缺少 SDK，安装 `Android SDK Platform 33` 与 `Build-Tools 33.0.2`。
3. 连接真机（开启 USB 调试）或启动模拟器，点击 Run。
4. 首页标题栏显示 `兰天浩2024110515`，下方有 4 个按钮。

## 三、功能对照

| 按钮 | Activity | 演示内容 |
| --- | --- | --- |
| 1. 正向传参 | `FirstActivity` | `Intent.putExtra` 传入姓名、学号并显示 |
| 2. 输入并返回数据 | `SecondActivity` | 输入内容后 `setResult` 返回，主界面显示结果 |
| 3. 隐式调用系统相机拍照 | `ThirdActivity` | `ACTION_IMAGE_CAPTURE` + `FileProvider`，拍照后显示照片 |
| 4. 秒表（横竖屏连续计时） | `FourthActivity` | 基于 `SystemClock.elapsedRealtime()`，`onSaveInstanceState` 保存恢复 |

```
app/src/main/java/com/lantianhao/multiactivity/
├── MainActivity.java      # 入口，4 个按钮 + onActivityResult
├── FirstActivity.java     # 正向传参
├── SecondActivity.java    # setResult 返回数据
├── ThirdActivity.java     # 隐式 Intent 调用相机 + FileProvider
└── FourthActivity.java    # 秒表 + 横竖屏状态保存恢复
app/src/main/res/values/strings.xml   # 全部界面字符串（无硬编码）
app/src/main/res/xml/file_paths.xml   # FileProvider 缓存目录映射
```

## 四、实验报告

实验报告位于 `docs/实验三_多Activity_2024110515_兰天浩.docx`（含运行截图）。

## 五、实现要点

- **字符串资源**：布局的 `text`/`hint`/`contentDescription` 与 Java 提示均引用 `strings.xml`。
- **相机**：`FileProvider` authority 为 `${applicationId}.fileprovider`，照片写入 `cacheDir/images/`，与 `file_paths.xml` 的 `<cache-path>` 对应。
- **横竖屏连续计时**：`onSaveInstanceState` 保存已累计时长、运行状态与起点，`onCreate` 恢复，旋转后不归零。
- **照片恢复**：`ThirdActivity` 保存照片路径，旋转屏幕后重新解码显示。
