# 实验六 多 Fragment 应用（MultiFragmentApp）

Android 移动应用开发 实验六：在实验五基础上用 Fragment 重构 APP，包含个人信息、2 张牌游戏、3 张牌游戏三个 Fragment，并用对象序列化保存历史信息。
应用标题（姓名+学号）：**兰天浩2024110515**。

## 一、实验要求对照

| 要求 | 实现 |
| --- | --- |
| 从 Dev 分支新建 Frag 分支，在同一仓库提交，保留 Main / Dev / Frag 三个分支 | 代码与报告提交在 `Frag` 分支，`Main` 分支通过 Pull Request 合并 |
| 至少 3 个 Fragment：个人信息、2 张牌翻牌游戏、3 张牌翻牌游戏 | `PersonFragment`、`TwoCardGameFragment`、`ThreeCardGameFragment`，由 `MainActivity` 底部按钮切换 |
| 个人信息采用序列化存储，退出后恢复 | `Player implements Serializable`，`ObjectStore` 使用 `openFileOutput` / `openFileInput` + `ObjectOutputStream` / `ObjectInputStream` 写入/读取 `player.dat` |
| 翻牌游戏采用序列化存储历史信息 | `GameRecord`（分数、最高分、牌面）序列化到 `game_two.dat` / `game_three.dat`，Fragment 创建时恢复 |
| 2 张牌与 3 张牌游戏 | `Card.match` 按其它牌数量判定：2 张同点 4 分/同花 1 分；3 张同点 8 分/同花 3 分 |
| 读取/写入文件使用 openFileInput / openFileOutput，对象流读写 | `data/ObjectStore` 统一封装 |
| 主题与字符串资源 | 主题使用 `Theme.MaterialComponents.DayNight.DarkActionBar.Bridge`；界面文字全部来自 `strings.xml` |
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

1. 用 Android Studio 打开 `实验六-多Fragment应用` 目录（或本仓库 `MultiFragmentApp`），等待 Gradle 同步。
2. 安装 `Android SDK Platform 33` 与 `Build-Tools 33.0.2`。
3. 连接真机或启动模拟器，点击 Run。
4. 主界面底部三个按钮切换“个人信息 / 2 张牌 / 3 张牌”；修改资料或玩游戏后退出，再次打开可恢复。

## 四、工程结构

```
app/src/main/java/com/lantianhao/multifragment/
├── controller/   MainActivity（Fragment 容器 + 关于菜单）
├── fragment/     PersonFragment、BaseGameFragment、TwoCardGameFragment、ThreeCardGameFragment
├── model/        Card、Deck、CardMatchingGame、GameRecord、Player
├── view/         CardView、CardAdapter
└── data/         ObjectStore（对象序列化文件读写）
app/src/main/res/
├── layout/       activity_main、fragment_person、fragment_game、item_card
├── menu/         menu_main.xml
├── values/       strings、colors、themes、integers
└── values-land/  integers（横屏牌数与列数）
app/src/test/            单元测试（23 个用例）
app/src/androidTest/     Espresso UI 测试（8 个用例）
docs/                    实验报告与界面预览图
```

## 五、Fragment 与 MVC

| 层 | 类 | 职责 |
| --- | --- | --- |
| Model | `Card`、`Deck`、`CardMatchingGame`、`GameRecord`、`Player` | 牌面与计分、牌局记录、玩家资料 |
| View | `activity_main`、`fragment_person`、`fragment_game`、`item_card`、`CardView`、`CardAdapter` | 容器布局、表单、RecyclerView 列表项 |
| Controller | `MainActivity`、三个 Fragment | 切换 Fragment、处理点击、读写序列化文件 |

MainActivity 通过 `FragmentTransaction.replace` 在三个 Fragment 间切换；每个 Fragment 在 `onViewCreated` 中从文件恢复数据，操作后与 `onPause` 时保存。

## 六、序列化存储

- 写文件：`context.openFileOutput(fileName, MODE_PRIVATE)` + `ObjectOutputStream.writeObject`；
- 读文件：`context.openFileInput(fileName)` + `ObjectInputStream.readObject`；
- 保存对象：`Player`（个人资料）与 `GameRecord`（分数、最高分、牌面 `List<Card>`）均实现 `Serializable`；
- 文件：`player.dat`、`game_two.dat`、`game_three.dat`。

## 七、游戏规则

- 2 张牌玩法：每次选 2 张，点数相同 4 分、花色相同 1 分；
- 3 张牌玩法：每次选 3 张，三张点数相同 8 分、三张花色相同 3 分；
- 每次选牌扣 1 分，配对成功按得分 ×4 加分，失败扣 2 分；
- 重置游戏会重新发牌并清零分数；最高分与牌面状态随 `GameRecord` 保存。

## 八、测试

```bash
gradlew :app:testDebugUnitTest            # 单元测试
gradlew :app:connectedDebugAndroidTest    # UI 测试（需真机/模拟器）
```

- 单元测试 23 个：CardTest 8、DeckTest 5、CardMatchingGameTest 8、GameRecordTest 2（含序列化往返）；
- UI 测试 8 个：MainActivityTest 4、PersonFragmentTest 1、GameFragmentTest 3；
- 本机无模拟器/真机，UI 测试只完成编译，未实际执行。

## 九、界面预览

`docs/images/` 提供 3 张按实际布局等比绘制的预览图：个人信息 Fragment、2 张牌游戏、3 张牌游戏。

> 说明：本机未安装模拟器、未连接真机，预览图用于说明界面结构；提交前可在真机/模拟器运行后替换为实际运行截图。

## 十、实验报告

`docs/实验六_多Fragment应用_2024110515_兰天浩.docx`（按学院《实验六+多Fragment应用》报告模板填写）。

## 十一、仓库与分支

- 仓库：<https://github.com/Lan829/2024110515-LanTianhao>
- 实验六目录（Frag 分支）：<https://github.com/Lan829/2024110515-LanTianhao/tree/Frag/实验六-多Fragment应用>
