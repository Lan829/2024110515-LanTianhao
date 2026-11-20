# 实验七 基于 Service 的音乐播放应用（MusicPlayerApp）

Android 移动应用开发 实验七：使用 ContentResolver 获取音乐列表，通过 Service + MediaPlayer 实现播放控制，SeekBar 实时显示进度，新歌播放时用 Notification 提示。
应用标题（姓名+学号）：**兰天浩2024110515**。

## 一、实验要求对照

| 要求 | 实现 |
| --- | --- |
| 使用 ContentResolver 获取音乐文件列表 | `MusicLoader` 查询 `MediaStore.Audio.Media`，筛选 `IS_MUSIC != 0`，读取歌曲名、艺术家、时长并生成 content Uri |
| 播放、暂停、停止、上一首、下一首均在服务中实现 | `MusicService` 内部使用 `MediaPlayer`，对外提供 `playSong / playOrPause / stop / next / previous / seekTo` |
| Activity 通过绑定服务获取 Binder，读取歌曲名、播放进度 | `bindService` + `MusicBinder`，Activity 定时读取 `getCurrentSong / getPosition / getDuration / isPlaying` |
| SeekBar 实时显示进度（多线程），拖动调整进度 | Activity 使用 Handler 每 500ms 刷新 SeekBar；拖动时暂停自动刷新，松手调用 `seekTo` |
| 新歌播放时 Notification 显示歌曲名 | `MusicService` 创建通知渠道，`OnPreparedListener` 中发送 `NotificationCompat` 通知 |
| Android 13+ 音频权限 | `READ_MEDIA_AUDIO`（API 33+）与 `READ_EXTERNAL_STORAGE`（≤32），以及 `POST_NOTIFICATIONS` |
| APP 标题为“姓名+学号” | `app_name` = 兰天浩2024110515 |

## 二、环境

| 项目 | 版本 |
| --- | --- |
| Android Studio | Hedgehog 及以上（AGP 7.4.2） |
| Gradle | 7.6（已带 wrapper） |
| JDK | 17 |
| 编译 / 目标 SDK | Android 13 / API 33 |
| 最低运行版本 | Android 7.0 / API 24 |
| 主要依赖 | RecyclerView 1.1.0、Material 1.9.0、AppCompat 1.6.1 |
| 测试 | JUnit 4.13.2、Espresso 3.5.1 |

## 三、运行步骤

1. 用 Android Studio 打开 `实验七-基于Service的音乐播放应用` 目录（或 `MusicPlayerApp`），等待 Gradle 同步。
2. 连接真机/模拟器，向设备放入若干音乐文件（或使用系统媒体库已有音频）。
3. 运行应用，首次启动授予“音频/通知”权限。
4. 点击歌曲开始播放，使用下方按钮控制播放/暂停/停止/上一首/下一首，拖动 SeekBar 调整进度。

## 四、工程结构

```
app/src/main/java/com/lantianhao/musicplayer/
├── controller/MainActivity（权限、歌曲列表、控制条、SeekBar）
├── service/MusicService（MediaPlayer + Binder + Notification）
├── model/Song（歌曲数据）
├── view/SongAdapter（RecyclerView 适配器）
└── util/MusicLoader、TimeUtils、PlaylistUtils
app/src/main/res/layout/       activity_main、item_song
app/src/main/res/drawable/     ic_play、ic_pause、ic_stop、ic_skip_previous、ic_skip_next 等
app/src/test/                  单元测试（7 个用例）
app/src/androidTest/           Espresso UI 测试（3 个用例）
docs/                          实验报告与界面预览图
```

## 五、关键实现

- **音乐列表**：`ContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ...)`，按标题排序，使用 `ContentUris.withAppendedId` 生成播放 Uri。
- **Service**：`MusicService` 在 `onCreate` 中创建 `MediaPlayer`，`onCompletion` 自动下一首；`MusicBinder` 返回服务实例，Activity 通过它读取状态并控制播放。
- **进度刷新**：Activity 的 `Handler` 每 500ms 读取 Binder 中的位置与时长；`userSeeking` 标记避免拖动时被定时刷新覆盖。
- **通知**：`NotificationChannel`（Android 8+）+ `NotificationCompat`，新歌准备完成后显示歌曲名与艺术家，点击返回播放界面。

## 六、测试

```bash
gradlew :app:testDebugUnitTest            # 单元测试
gradlew :app:connectedDebugAndroidTest    # UI 测试（需真机/模拟器）
```

- 单元测试 7 个：TimeUtilsTest 4、PlaylistUtilsTest 3，全部通过；
- UI 测试 3 个：播放控件显示、播放按钮点击、关于对话框，已编译通过；本机无设备未执行。

## 七、界面预览

`docs/images/` 提供 2 张按实际布局等比绘制的预览图：播放界面（歌曲列表 + SeekBar + 控制按钮）、通知栏显示当前歌曲。

> 说明：本机未安装模拟器、未连接真机，预览图用于说明界面结构；提交前可在真机/模拟器运行后替换为实际运行截图。

## 八、实验报告

`docs/实验七_基于Service的音乐播放应用_2024110515_兰天浩.docx`（按学院《实验七+基于Service的音乐播放应用》报告模板填写）。

## 九、仓库与分支

- 仓库：<https://github.com/Lan829/2024110515-LanTianhao>
- 实验七目录（Service 分支）：<https://github.com/Lan829/2024110515-LanTianhao/tree/Service/实验七-基于Service的音乐播放应用>
