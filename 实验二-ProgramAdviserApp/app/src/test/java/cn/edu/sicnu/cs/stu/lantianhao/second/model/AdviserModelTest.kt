package cn.edu.sicnu.cs.stu.lantianhao.second.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Model 层单元测试：不启动界面，直接检查查询逻辑是否符合预期。
 *
 * 运行方式：./gradlew testDebugUnitTest
 */
class AdviserModelTest {

    private val model = AdviserModel(
        listOf(
            AdviserModel.Topic("循环", "重复的工作交给循环"),
            AdviserModel.Topic("函数", "把重复代码抽成函数"),
            AdviserModel.Topic("MVC", "Model 管数据、View 管显示、Controller 管协调")
        )
    )

    /** 输入包含某个关键字时，应该命中并返回对应建议 */
    @Test
    fun matchedWhenInputContainsKeyword() {
        val result = model.advise("循环怎么写")
        assertTrue(result is AdviserModel.AdviserResult.Matched)
        assertEquals("循环", (result as AdviserModel.AdviserResult.Matched).topic.keyword)
    }

    /** 输入为空或只有空格时，应该返回 Empty */
    @Test
    fun emptyWhenInputIsBlank() {
        assertEquals(AdviserModel.AdviserResult.Empty, model.advise("   "))
    }

    /** 没有收录的关键字应该返回 NotFound，并把原始输入带回去 */
    @Test
    fun notFoundWhenNoKeywordMatched() {
        val result = model.advise("量子计算")
        assertTrue(result is AdviserModel.AdviserResult.NotFound)
        assertEquals("量子计算", (result as AdviserModel.AdviserResult.NotFound).query)
    }

    /** 英文关键字应该忽略大小写匹配 */
    @Test
    fun matchedIgnoringCase() {
        assertTrue(model.advise("mvc 是三层架构") is AdviserModel.AdviserResult.Matched)
    }

    /** keywords() 应该按顺序返回全部主题关键字，供界面生成快捷按钮 */
    @Test
    fun keywordsReturnAllTopics() {
        assertEquals(listOf("循环", "函数", "MVC"), model.keywords())
    }
}
