import org.jsoup.Jsoup
import javax.mail.BodyPart
import javax.mail.Multipart
import javax.mail.Part

interface MimeParser {
    fun parse(content: Any, matcher: ContentExtractor): Content?

}

abstract class StringMimeParser : MimeParser {
    final override fun parse(content: Any, matcher: ContentExtractor): Content = parse(content as String, matcher)
    abstract fun parse(content: String, matcher: ContentExtractor): Content
}

fun mapContent(converter: (String) -> String): StringMimeParser = object : StringMimeParser() {
    override fun parse(content: String, matcher: ContentExtractor): Content = Content(converter(content))
}

fun parseHtml(html: String): String {
    val document = Jsoup.parse(html)
    return HtmlToPlainText().getPlainText(document)
}

class MessageParser : MimeParser {
    override fun parse(content: Any, matcher: ContentExtractor): Content? {
        val part = content as Part
        return matcher.extract(part, true)
    }
}

abstract class AbstractMultipartParser : MimeParser {
    final override fun parse(content: Any, matcher: ContentExtractor): Content? = parse(content as Multipart, matcher)
    abstract fun parse(content: Multipart, matcher: ContentExtractor): Content?
}


class MultipartParser : AbstractMultipartParser() {
    override fun parse(content: Multipart, matcher: ContentExtractor): Content {
        return Content(buildString {
            content.iterParts { bodyPart ->
                val text = matcher.extract(bodyPart, true)?.text
                if (text != null) {
                    appendln(text)
                }
            }
        })
    }
}

class MultipartAlternativeParser : AbstractMultipartParser() {
    override fun parse(content: Multipart, matcher: ContentExtractor): Content? {
        // at first we try to extract known alternative, only after
        var nonExactContent : Content? = null
        content.iterParts { bodyPart ->
            val partContent = matcher.extract(bodyPart, true) ?: return@iterParts
            if (partContent.isExact) return partContent
            nonExactContent = partContent
        }
        return nonExactContent
    }
}

inline fun Multipart.iterParts(f: (BodyPart) -> Unit) {
    for (i in 0 until count) {
        val bodyPart = getBodyPart(i)
        f(bodyPart)
    }
}


