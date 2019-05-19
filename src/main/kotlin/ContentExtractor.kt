import javax.mail.Part
import javax.mail.internet.MimeMessage

class Content(val text: String, val isExact: Boolean = true)

class ContentExtractor(private val parsers: List<MimeParserInfo>) {
    fun extract(messagePart: Part, handleUnknownType: Boolean): Content? {
        if (Part.ATTACHMENT.equals(messagePart.disposition, true)) return null
        val content = messagePart.content
        for (info in parsers) {
            if (messagePart.isMimeType(info.contentType)) {
                return info.parser.parse(content, this)
            }
        }

        if (handleUnknownType && content is String) {
            return Content(content, false)
        }
        return null
    }
}

class MimeParserInfo(val contentType: String, val parser: MimeParser)

val contentExtractor = ContentExtractor(
    listOf(
        MimeParserInfo("text/plain", parser = mapContent { it }),
        MimeParserInfo("text/html", parser = mapContent { parseHtml(it) }),
        MimeParserInfo("multipart/alternative", parser = MultipartAlternativeParser()),
        MimeParserInfo("multipart/*", parser = MultipartParser()),
        MimeParserInfo("message/rfc822", parser = MessageParser())
    )
)

fun extractText(mimeMessage: MimeMessage): String {
    return contentExtractor.extract(mimeMessage, true)?.text ?: ""
}
