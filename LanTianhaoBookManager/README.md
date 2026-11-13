# LanTianhaoBookManager · 书籍信息管理 APP

四川师范大学 计算机科学学院 2024 级 Android 开发 **半期上机过程考核** 项目。

| 项目 | 内容 |
| --- | --- |
| 姓名 / 学号 | 兰天浩 / 2024110515 |
| 题目 | 实现一个由两个 Activity 组成的书籍信息管理 APP |
| 仓库 | https://github.com/Lan829/2024110515-LanTianhao |
| 工程目录 | `LanTianhaoBookManager`（项目名包含姓名拼音） |
| 文字颜色 | 学号尾号 **5** → **蓝色**（`#0000FF`，深色模式为 `#82B1FF`） |
| 编译 / 目标 SDK | 33（Android 13） |
| 最低运行版本 | 24（Android 7.0） |

## 一、试题要求对照

| 试题要求 | 实现位置 |
| --- | --- |
| 项目名包含姓名拼音 | `settings.gradle`：`rootProject.name = "LanTianhaoBookManager"` |
| 书籍类（书名、作者、出版社、价格等） | `model/Book.java`，另扩展 ISBN、类别、出版年份、评分、简介 |
| 第 1 个 Activity 在代码中新建多个书籍对象并放入数组 | `data/BookRepository.java`：`private static final Book[] BOOKS = {...}`（14 本） |
| 对数组中的书籍按价格从低到高排序 | `model/BookSorter.java` + `SortOption.PRICE_ASC`，`MainActivity` 启动时排序 |
| 采用列表显示书籍 | `RecyclerView` + `ui/BookAdapter.java` + `item_book.xml` |
| 单击书籍启动第 2 个 Activity | `MainActivity#openDetail()` 通过 `Intent` 传递 `Book` |
| 第 2 个 Activity 显示完整信息 | `ui/DetailActivity.java` + `activity_detail.xml` |
| 第 1 个 Activity 标题为“姓名+学号” | `strings.xml`：`main_title = 兰天浩2024110515` |
| 界面布局合理、代码规范 | Material 组件、资源文件统一管理、分层结构、25 个单元测试 |
| APP 文字颜色按学号尾号设置 | 尾号 5 → 蓝色：`values/colors.xml`、`values-night/colors.xml`、`themes.xml` 全局设置 |

## 二、功能清单

### 必做功能

1. **双 Activity 架构**：列表页 `MainActivity` → 详情页 `DetailActivity`。
2. **代码内数据源**：14 本书籍直接以 `Book[]` 数组创建，不依赖数据库或网络。
3. **价格排序**：默认按价格从低到高，价格相同时按书名排序，保证结果稳定。
4. **列表展示**：RecyclerView 卡片列表，显示书名、作者、出版社、价格、类别与收藏状态。
5. **点击进入详情**：通过 `Serializable` 传递完整 `Book` 对象，详情页展示全部字段。
6. **蓝色文字**：学号尾号 5 → 蓝色；主题统一设置默认文字颜色，所有页面均生效。

### 拓展功能

| 功能 | 说明 |
| --- | --- |
| 关键词搜索 | 支持书名、作者、出版社、ISBN、类别模糊匹配，忽略大小写 |
| 三种排序 | 价格升序（默认）、价格降序、书名升序，菜单可切换 |
| 收藏 | 列表星标与详情页按钮均可收藏，`SharedPreferences` 持久化 |
| 只看收藏 | 与搜索、排序组合使用，空结果时显示空状态页 |
| 统计信息 | 实时显示当前结果的数量、总价、均价 |
| 详情分享 | 调用系统分享面板分享书籍完整信息 |
| 深色模式 | 提供 `values-night` 资源，深色下仍保持蓝色文字并保证可读性 |
| 状态保存 | 屏幕旋转后保留搜索词、排序方式与“只看收藏”状态 |
| 关于对话框 | 展示姓名、学号、颜色规则与功能说明 |
| 单元测试 | 25 个 JVM 测试，覆盖模型、排序、筛选、数据源、金额格式化 |
| 空状态与动效 | 无结果提示；RecyclerView DiffUtil 列表动画 |

## 三、工程结构

```text
LanTianhaoBookManager/
├── app/src/main/java/com/lantianhao/bookmanager/
│   ├── model/        Book（Serializable 实体）、BookSorter、SortOption
│   ├── data/         BookRepository（Book[] 数据源）、BookFilter、FavoriteStore
│   ├── ui/           MainActivity、DetailActivity、BookAdapter
│   └── util/         MoneyUtils（人民币格式化）
├── app/src/main/res/
│   ├── layout/       activity_main、activity_detail、item_book
│   ├── menu/         menu_main（排序 / 只看收藏 / 关于）
│   ├── values/       colors、strings、themes
│   ├── values-night/ 深色模式颜色与主题
│   └── drawable/     矢量图标与背景
├── app/src/test/     25 个单元测试
├── docs/             实验报告素材与截图清单
├── build.gradle
├── settings.gradle
└── README.md
```

## 四、核心实现

### 1. 在代码中创建书籍数组

```java
private static final Book[] BOOKS = {
        new Book("Java编程思想（第4版）", "Bruce Eckel", "机械工业出版社", 108.00,
                "9787111213826", "计算机", 2007, 9.1f, "……"),
        new Book("活着", "余华", "作家出版社", 28.00,
                "9787506365437", "文学", 2012, 9.4f, "……"),
        // 共 14 本
};
```

### 2. 按价格从低到高排序

```java
Comparator<Book> byTitle =
        Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
Comparator<Book> byPriceAsc =
        Comparator.comparingDouble(Book::getPrice).thenComparing(byTitle);
Collections.sort(books, byPriceAsc);
```

### 3. 列表点击进入第 2 个 Activity

```java
private void openDetail(Book book) {
    Intent intent = new Intent(this, DetailActivity.class);
    intent.putExtra(DetailActivity.EXTRA_BOOK, book);
    startActivity(intent);
}
```

### 4. 学号尾号 5 → 蓝色文字

```xml
<!-- values/colors.xml -->
<color name="text_blue">#0000FF</color>

<!-- themes.xml：全局默认文字颜色 -->
<item name="android:textColor">@color/text_blue</item>
<item name="android:textColorPrimary">@color/text_blue</item>
<item name="colorOnSurface">@color/text_blue</item>
```

深色模式在 `values-night/colors.xml` 中使用 `#82B1FF`，仍然属于蓝色系。

## 五、运行方式

1. 使用 Android Studio 打开 `LanTianhaoBookManager` 目录。
2. 安装 Android SDK Platform 33 与 Build-Tools 33.0.2（项目已带 Gradle 7.6 Wrapper）。
3. 等待 Gradle 同步完成，连接真机或启动模拟器，点击 Run。
4. 首页标题应为 **兰天浩2024110515**，列表默认按价格升序排列。

> `local.properties` 记录本机 SDK 路径，已被 `.gitignore` 排除；Android Studio 首次打开会自动生成。

## 六、构建与测试

```bash
# 构建 Debug APK
./gradlew assembleDebug

# 运行全部单元测试
./gradlew testDebugUnitTest
```

本次交付验证结果：

- `assembleDebug` 构建成功，生成 `app/build/outputs/apk/debug/app-debug.apk`；
- 25 个单元测试全部通过（0 failures）；
- 核心测试覆盖：Book 字段/序列化、三种排序、价格相同稳定性、ISBN 唯一性、搜索与收藏组合筛选、金额格式化。

## 七、提交记录

按考核要求，本工程至少有两次提交：

1. 新建项目并完成双 Activity、书籍数组、价格排序、列表与蓝色文字；
2. 检查前补充单元测试、README、实验报告素材并完成最终构建。

## 八、实验报告

`docs/上机测试实验报告素材.md` 已按学院模板整理好“实验内容 / 核心代码 / 结果分析”等文字材料；
`docs/运行截图清单.md` 列出了建议在真机或模拟器上拍摄的界面。报告由本人提交到智慧树。
