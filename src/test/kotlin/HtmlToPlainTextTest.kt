import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Test

class HtmlToPlainTextTest {
    @Test
    fun htmlToPlainText() {
        val document = Jsoup.parse(HtmlToPlainTextTest::class.java.getResource("example.html").readText())
        val actual = HtmlToPlainText().getPlainText(document)
        document.text()
        assertEquals(
            "Example Domain\n" +
                    "Example Domain\n\n" +
                    "This domain is established to be used for illustrative examples in documents. " +
                    "You may use this domain in examples without prior coordination or asking for permission.\n\n" +
                    "More information...http://www.iana.org/domains/example\n",
            actual
        )
    }

}