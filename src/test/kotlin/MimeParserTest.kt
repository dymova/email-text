import org.junit.Test
import javax.mail.Session
import javax.mail.internet.MimeMessage
import kotlin.test.assertEquals

class MimeParserTest {
    @Test
    fun extractFromTextPlainMessage() {
        testExtractText("This is a plain text/plain message.  Nothing fancy here...", "TextPlain.txt")
    }

    @Test
    fun extractFromTextHtmlMessage() {
        testExtractText(
            "This is the HTML part.\n" +
                    "It should be displayed inline.", "TextHtml.txt"
        )
    }

    @Test
    fun extractFromMultipartMixedMessage() {
        testExtractText(
            "Have fun!\n" +
                    "This is a plain text/plain message.  Nothing fancy here...\n", "MultipartMixed.txt"
        )
    }


    @Test
    fun extractFromMessageRfc822Message() {
        testExtractText(
            "This is a plain text/plain message.  Nothing fancy here...", "MessageRfc822.txt"
        )
    }

    // TODO станные переводы строк в конце
    @Test
    fun extractFromTextHtmlWithAttachmentMessage() {
        testExtractText(
            "\n What do we have here... This message is an HTML message. " +
                    "Also attached is an HTML FILE. Both of these are in a multipart/mixed part.\n\n\n",
            "TextHtmlAttachment.txt"
        )
    }

    @Test
    fun extractFromMultipartAlternativeMessage() {
        testExtractText(
            "... plain text version of message goes here ...\n",
            "MultipartAlternative.txt"
        )
    }


    private fun testExtractText(expected: String, fileName: String) {
        val session = Session.getDefaultInstance(System.getProperties())
        val mimeMessage = MimeMessage(session, MimeParserTest::class.java.getResourceAsStream(fileName))
        val actual = extractText(mimeMessage)
        assertEquals(expected, actual)
    }
}