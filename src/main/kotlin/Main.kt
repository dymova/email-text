import org.jsoup.Jsoup
import java.io.FileInputStream
import javax.mail.Multipart
import javax.mail.Part
import javax.mail.Session
import javax.mail.internet.MimeMessage

fun main(args: Array<String>) {
    val session = Session.getDefaultInstance(System.getProperties())
    val fis = FileInputStream("/Users/nastya/IdeaProjects/mime-message/src/main/resources/Multipart.txt")
    val mimeMessage = MimeMessage(session, fis)
    println(extractText(mimeMessage))
}





