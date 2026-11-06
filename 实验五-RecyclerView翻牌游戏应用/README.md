# 实验五 RecyclerView 翻牌游戏应用（RecyclerViewCardGame）

Android 移动应用开发 实验五：在实验四基础上，用 RecyclerView 重写翻牌游戏，并加入菜单、关于对话框与三张牌玩法。
应用标题（姓名+学号）：**兰天浩2024110515**。

## 一、实验要求对照

| 要求 | 实现 |
| --- | --- |
| git 新建分支 Dev，在 Dev 分支完成并提交（同一仓库两个分支 Main / Dev） | 代码与报告提交在 `Dev` 分支，`Main` 分支通过 Pull Request 合并 |
| 修改分支权限，Main 禁止直接 push，通过 PR 审核合并 | GitHub 上设置 `Main` 分支保护（Require a pull request before merging），实验五通过 Pull Request 合并 |
| MainActivity 添加菜单，点击弹出关于对话框 | 选项菜单「关于」，使用 `MaterialAlertDialogBuilder` 显示应用信息 |
| RecyclerView 实现 24 张牌，界面规整美观 | 竖屏 `GridLayoutManager` 4 列 × 6 行 = 24 张；横屏 8 列 × 4 行 = 32 张 |
| 适配器从 RecyclerView.Adapter 派生，重载方法并处理回调 | `CardAdapter` 重载 `onCreateViewHolder / onBindViewHolder / getItemCount`，通过 `OnCardClickListener` 回调点击 |
| 模型添加 reset 方法 | `CardMatchingGame.reset()`：分数归零、清除牌面状态并重新发牌 |
| 控制器支持横竖屏不同牌数 | `values/integers.xml` 与 `values-land/integers.xml` 提供不同 `card_count`、`span_count` |
| 三张牌游戏（只改 chooseCardAtIndex 与 match） | 选满 3 张判定：三张同点 / 同花即配对，规则见下文 |
| 主题与字符串资源 | 主题使用 `Theme.MaterialComponents.DayNight.DarkActionBar.Bridge`；界面文字全部来自 `strings.xml` |

## 二、环境

| 项目 | 版本 |
| --- | --- |
| Android Studio | Hedgehog 及以上（AGP 7.4.2） |
| Gradle | 7.6（已带 wrapper） |
| JDK | 17 |
| 编译 / 目标 SDK | Android 13 / API 33 |
| 最低运行版本 | Android 7.0 / API 24 |
| 主要依赖 | RecyclerView 1.3.1、Material 1.9.0、AppCompat 1.6.1 |
| 测试 | JUnit 4.13.2、Espresso 3.5.1（含 espresso-contrib） |

## 三、运行步骤

1. 用 Android Studio 打开 `实验五-RecyclerView翻牌游戏应用` 目录（或本仓库 `RecyclerViewCardGame`），等待 Gradle 同步。
2. 安装 `Android SDK Platform 33` 与 `Build-Tools 33.0.2`。
3. 连接真机或启动模拟器，点击 Run。
4. 主界面标题为 `兰天浩2024110515`，右上角菜单可打开「关于」对话框；点击「翻牌游戏」进入 24 张牌（横屏 32 张）。

## 四、工程结构

```
app/src/main/java/com/lantianhao/recyclercardgame/
├── model/        Card、Deck、CardMatchingGame（三张牌玩法 + reset）、Player
├── view/         CardView（牌面三态）、CardAdapter（RecyclerView 适配器）
├── controller/   MainActivity（菜单/对话框）、PersonInfoActivity、CardGameActivity
└── data/         HighScoreStore（最高分）
app/src/main/res/
├── layout/       activity_main、activity_person_info、activity_card_game、item_card
├── menu/         menu_main.xml（关于菜单）
├── values/       strings、colors、themes、integers（竖屏 24 张 / 4 列）
└── values-land/  integers（横屏 32 张 / 8 列）
app/src/test/            单元测试（20 个用例）
app/src/androidTest/     Espresso UI 测试（12 个用例）
docs/                    实验报告与界面预览图
```

## 五、MVC 与 RecyclerView

| 层 | 类 | 职责 |
| --- | --- | --- |
| Model | `Card`、`Deck`、`CardMatchingGame`、`Player` | 牌面、发牌、三张牌判定、计分与重置 |
| View | `activity_*.xml`、`item_card.xml`、`CardView`、`CardAdapter` | 界面布局、单张牌外观、列表项绑定 |
| Controller | `MainActivity`、`PersonInfoActivity`、`CardGameActivity` | 菜单与对话框、点击回调、模型与界面同步 |

Adapter 工作流程：`CardGameActivity` 创建 `GridLayoutManager` 与 `CardAdapter` → `onCreateViewHolder` 加载 `item_card.xml` → `onBindViewHolder` 把模型状态绑定到 `CardView` 并设置点击回调 → 点击时 Activity 调用 `chooseCardAtIndex`，再 `notifyDataSetChanged()` 刷新。

## 六、三张牌玩法

- 每次选牌扣 1 分；
- 选满 3 张后判定：三张点数全部相同得 8 分，三张花色全部相同得 3 分，其他情况为 0 分；
- 配对成功：得分 × `MATCH_BONUS`(4)，三张牌标记为已配对；
- 配对失败：扣 2 分，取消前两张牌的选中状态，第三张保持选中；
- 已配对的牌不可再选；再次点击已选中的牌可取消选中且不扣分；
- 「重置游戏」调用 `CardMatchingGame.reset()` 重新发牌并清零分数。

## 七、横竖屏牌数

- 竖屏：`values/integers.xml` → `card_count = 24`、`span_count = 4`（4×6）；
- 横屏：`values-land/integers.xml` → `card_count = 32`、`span_count = 8`（8×4）；
- Activity 根据 RecyclerView 可用高度与行数动态设置每张牌高度，保证不同方向下牌面规整。

## 八、测试

```bash
gradlew :app:testDebugUnitTest            # 单元测试
gradlew :app:connectedDebugAndroidTest    # UI 测试（需真机/模拟器）
```

- 单元测试 19 个：CardTest 6（三张同点/同花/混合、参数校验等）、DeckTest 5、CardMatchingGameTest 8（三张配对、失败扣分、reset、结束判定等）；
- UI 测试 11 个：主界面信息与菜单对话框、个人信息保存/取消、RecyclerView 24 张牌、点击扣分、重置游戏。
- 本机无模拟器/真机，UI 测试只完成编译，未实际执行。

## 九、界面预览

`docs/images/` 提供 4 张按实际布局等比绘制的预览图：主界面、关于对话框、竖屏 24 张牌、横屏 32 张牌。

> 说明：本机未安装模拟器、未连接真机，预览图用于说明界面结构；提交前可在真机/模拟器运行后替换为实际运行截图。

## 十、实验报告

`docs/实验五_RecyclerView翻牌游戏应用_2024110515_兰天浩.docx`（按学院《实验五+RecyclerView翻牌游戏应用》报告模板填写）。

## 十一、仓库与分支

- 仓库：<https://github.com/Lan829/2024110515-LanTianhao>
- 实验五目录（Dev 分支）：<https://github.com/Lan829/2024110515-LanTianhao/tree/Dev/实验五-RecyclerView翻牌游戏应用>
