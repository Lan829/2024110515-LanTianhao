# 实验四 翻牌游戏应用（CardMatchingGame）

Android 移动应用开发 实验四：1 个主 Activity + 2 个二级 Activity，采用 MVC 分层实现个人信息展示、个人信息修改与可交互的翻牌游戏。
应用标题（姓名+学号）：**兰天浩2024110515**。

## 一、实验要求对照

| 要求 | 实现 |
| --- | --- |
| 主 Activity 显示玩家基本信息（姓名、性别、电话、最高分等） | `MainActivity` + `activity_main.xml`：头像、姓名、性别、学号、电话、邮件地址、爱好、游戏最高分 |
| 信息修改 Activity（修改后无需保存到文件） | `PersonInfoActivity`：文本框、性别单选、爱好多选、相册选头像；保存后通过 `setResult` 回传，不写文件 |
| 翻牌游戏 Activity（View / Model / Controller，可重新开始） | `CardGameActivity` + `CardView` + `Card`、`Deck`、`CardMatchingGame`；“重置游戏”新建一局 |
| Model 参照附件代码 | `Card.match()`（同点 4 分 / 同花 1 分）、`Deck.drawRandomCard()`、`CardMatchingGame.chooseCardAtIndex()` 按附件 Kotlin 代码等价移植为 Java |
| 单元测试与 UI 测试 | JUnit 19 个用例（已通过）+ Espresso 11 个用例（需真机/模拟器执行） |
| 禁止硬编码字符串 | 布局与 Java 代码中的界面文字全部来自 `res/values/strings.xml` |
| 主题要求 | `themes.xml` 使用 `Theme.MaterialComponents.DayNight.DarkActionBar.Bridge` |
| APP 标题为“姓名+学号” | `app_name` = 兰天浩2024110515 |

## 二、环境要求

| 项目 | 版本 |
| --- | --- |
| Android Studio | Hedgehog 及以上（支持 AGP 7.4.2） |
| Gradle | 7.6（已带 wrapper） |
| JDK | 17 |
| 编译 / 目标 SDK | Android 13 / API 33 |
| 最低运行版本 | Android 7.0 / API 24 |
| 开发语言 | Java |
| 测试 | JUnit 4.13.2、Espresso 3.5.1 |

## 三、运行步骤

1. 用 Android Studio 打开 `实验四-CardMatchingGame` 目录，等待 Gradle 同步。
2. 若提示缺少 SDK，安装 `Android SDK Platform 33` 与 `Build-Tools 33.0.2`。
3. 连接真机（开启 USB 调试）或启动模拟器，点击 Run。
4. 应用标题栏显示 `兰天浩2024110515`，主界面可进入“修改个人信息”和“翻牌游戏”。

## 四、工程结构

```
app/src/main/java/com/lantianhao/cardgame/
├── model/        Card、Deck、CardMatchingGame、Player
├── view/         CardView（牌面：背面 / 选中 / 已配对）
├── controller/   MainActivity、PersonInfoActivity、CardGameActivity
└── data/         HighScoreStore（最高分 SharedPreferences）
app/src/main/res/
├── layout/       activity_main、activity_person_info、activity_card_game
├── values/       strings.xml、colors.xml、themes.xml
└── drawable/     牌面背景、头像、相机图标等
app/src/test/            单元测试（19 个用例）
app/src/androidTest/     Espresso UI 测试（11 个用例）
docs/                    实验报告与界面预览图
```

## 五、MVC 设计

| 层 | 类 | 职责 |
| --- | --- | --- |
| Model | `Card`、`Deck`、`CardMatchingGame`、`Player` | 牌面、随机牌堆、计分规则、玩家资料 |
| View | `activity_*.xml`、`CardView` | 界面布局与单张牌的外观 |
| Controller | `MainActivity`、`PersonInfoActivity`、`CardGameActivity` | 接收点击、调用模型、同步界面 |

交互流程：点击某张牌 → `CardGameActivity` 调用 `CardMatchingGame.chooseCardAtIndex(index)` → 模型更新选中/配对状态与得分 → Controller 调用所有 `CardView.render()` 同步牌面并刷新得分。

## 六、游戏规则

- 每次选牌扣 1 分（`COST_TO_CHOOSE = 1`）；
- 与已选中的牌点数相同：配对得分 4，乘以 `MATCH_BONUS = 4`，两张牌变为已配对；
- 花色相同：配对得分 1，乘以 4；
- 配对失败扣 2 分（`MISMATCH_PENALTY = 2`），并取消上一张牌的选中状态；
- 再次点击已选中的牌会取消选中，不扣分；已配对的牌不可再选；
- “重置游戏”重新抽取 16 张牌开始新的一局；“退出”返回主界面；
- 单局得分超过历史最高分时写入 `SharedPreferences`，主界面 `onResume` 刷新显示。

## 七、测试

### 单元测试（已执行通过）

```bash
gradlew :app:testDebugUnitTest
```

结果：19 个用例全部通过（`CardTest` 6 + `DeckTest` 5 + `CardMatchingGameTest` 8，0 失败 0 错误）。

### UI 测试（需真机 / 模拟器）

```bash
gradlew :app:connectedDebugAndroidTest
```

包含 `MainActivityTest` 5 个、`PersonInfoActivityTest` 2 个、`CardGameActivityTest` 4 个用例。本机没有可用模拟器/真机，因此只完成了测试代码编译，未实际执行。

## 八、界面预览

`docs/images/` 下有 4 张按实际布局（1080×2340）等比例绘制的界面预览图：主界面、个人信息界面、翻牌游戏初始界面、翻牌游戏进行中。

> 说明：本机未安装 Android 模拟器、也未连接真机，预览图用于说明界面结构；提交前可在真机/模拟器运行后替换为实际运行截图。

## 九、实验报告

实验报告：`docs/实验四_翻牌游戏应用_2024110515_兰天浩.docx`（按学院《实验四+翻牌游戏应用》报告模板填写：封面信息、实验目的与要求、实验内容、核心代码与运行界面、实验结果分析与评价，并补充开发环境、MVC 说明与测试结果）。

## 十、仓库地址

<https://github.com/Lan829/2024110515-LanTianhao/tree/main/实验四-CardMatchingGame>
