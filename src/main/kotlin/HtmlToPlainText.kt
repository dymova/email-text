import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.NodeTraversor
import org.jsoup.select.NodeVisitor


class HtmlToPlainText {
    fun getPlainText(element: Element): String {
        val formatter = FormattingVisitor()
        NodeTraversor.traverse(formatter, element)

        return formatter.toString()
    }

    private class FormattingVisitor : NodeVisitor {
        private val accum = StringBuilder()

        override fun head(node: Node, depth: Int) {
            val name = node.nodeName()
            when {
                node is TextNode -> {
                    val text = node.text()
                    if(!text.isBlank()){
                        accum.append(text)
                    }
                }
                name == "li" -> accum.append("\n * ")
                name == "dt" -> accum.append("  ")
                name in newlineOpenIntroducingTags -> accum.append("\n")
            }
        }

        override fun tail(node: Node, depth: Int) {
            when (node.nodeName()) {
                in newlineClosingIntroducingTags -> accum.append("\n")
                "a" -> accum.append(node.absUrl("href"))
            }
        }

        override fun toString(): String {
            return accum.toString()
        }

        companion object {
            private val newlineOpenIntroducingTags = listOf("p", "h1", "h2", "h3", "h4", "h5", "tr")
            private val newlineClosingIntroducingTags = listOf("br", "dd", "dt", "p", "h1", "h2", "h3", "h4", "h5")
        }
    }
}