package cn.edu.sicnu.cs.stu.lantianhao.second.model

/**
 * MVC 中的 Model 层：程序顾问的数据与查询逻辑。
 *
 * 这一层只关心"数据长什么样、怎么查"，不引用 Context、不引用任何控件，
 * 因此可以脱离界面单独测试，也方便以后换成网络或数据库数据源。
 */
class AdviserModel(private val topics: List<Topic>) {

    /** 一条咨询规则：主题关键字 + 对应的建议 */
    data class Topic(val keyword: String, val advice: String)

    /** 查询结果的三种情况，交给 Controller 决定怎么显示 */
    sealed class AdviserResult {
        /** 命中某条规则 */
        data class Matched(val topic: Topic) : AdviserResult()

        /** 用户没有输入内容 */
        data object Empty : AdviserResult()

        /** 没有收录该主题 */
        data class NotFound(val query: String) : AdviserResult()
    }

    /**
     * 按照用户输入查询建议。
     * 规则：忽略大小写，只要输入内容里包含某个主题关键字就算命中。
     */
    fun advise(input: String): AdviserResult {
        val query = input.trim()

        if (query.isEmpty()) {
            return AdviserResult.Empty
        }

        val hit = topics.firstOrNull { topic ->
            query.contains(topic.keyword, ignoreCase = true)
        }

        return if (hit == null) {
            AdviserResult.NotFound(query)
        } else {
            AdviserResult.Matched(hit)
        }
    }

    /** 所有主题关键字，供界面生成快捷按钮使用 */
    fun keywords(): List<String> = topics.map { it.keyword }
}
