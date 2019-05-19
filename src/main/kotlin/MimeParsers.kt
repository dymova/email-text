import org.jsoup.Jsoup
import javax.mail.BodyPart
import javax.mail.Multipart
import javax.mail.Part

interface MimeParser {
    fun parse(content: Any, matcher: ContentExtractor): String?

}

abstract class StringMimeParser : MimeParser {
    final override fun parse(content: Any, matcher: ContentExtractor): String = parse(content as String, matcher)
    abstract fun parse(content: String, matcher: ContentExtractor): String
}

fun mapContent(stringConverter: (String) -> String): StringMimeParser = object : StringMimeParser() {
    override fun parse(content: String, matcher: ContentExtractor): String = stringConverter(content)
}

fun parseHtml(html: String): String {
    val document = Jsoup.parse(html)
    return HtmlToPlainText().getPlainText(document)
}

class MessageParser : MimeParser {

    override fun parse(content: Any, matcher: ContentExtractor): String? {
        val part = content as Part
        return matcher.extract(part, true)
    }
}

abstract class AbstractMultipartParser : MimeParser {
    final override fun parse(content: Any, matcher: ContentExtractor): String? = parse(content as Multipart, matcher)
    abstract fun parse(content: Multipart, matcher: ContentExtractor): String?
}


class MultipartParser : AbstractMultipartParser() {
    override fun parse(content: Multipart, matcher: ContentExtractor): String {
        return buildString {
            content.iterParts { bodyPart ->
                val text = matcher.extract(bodyPart, true)
                if (text != null) {
                    appendln(text)
                }
            }
        }
    }
}

class MultipartAlternativeParser : AbstractMultipartParser() {
    override fun parse(content: Multipart, matcher: ContentExtractor): String? {
        // at first we try to extract known alternative, only after
        return parse(content, matcher, false)
            ?: parse(content, matcher, true)
    }

    private fun parse(multipart: Multipart, matcher: ContentExtractor, handleUnknownType: Boolean) : String? {
        multipart.iterParts { bodyPart ->
            return matcher.extract(bodyPart, handleUnknownType) ?: return@iterParts
        }
        return null
    }
}

inline fun Multipart.iterParts(f: (BodyPart) -> Unit) {
    for (i in 0 until count) {
        val bodyPart = getBodyPart(i)
        f(bodyPart)
    }
}


