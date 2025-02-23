package com.cricketapp.hackfusion

import android.os.AsyncTask
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class JavaMailAPI(
    private val email: String,
    private val subject: String,
    private val message: String,
    private val callback: ((Boolean) -> Unit)? = null // Callback to check success
) : AsyncTask<Void, Void, Boolean>() {

    override fun doInBackground(vararg params: Void?): Boolean {
        return try {
            val props = Properties()
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.starttls.enable"] = "true"
            props["mail.smtp.host"] = "smtp.gmail.com"
            props["mail.smtp.port"] = "587"

            val session = Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication("shivamkachawar@gmail.com", "zwocepkeeogcxyph") // Use App Password
                }
            })

            val mimeMessage = MimeMessage(session)
            mimeMessage.setFrom(InternetAddress("shivamkachawar@gmail.com"))
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
            mimeMessage.subject = subject
            mimeMessage.setText(message)

            Transport.send(mimeMessage)
            true
        } catch (e: MessagingException) {
            e.printStackTrace()
            false
        }
    }

    override fun onPostExecute(success: Boolean) {
        callback?.invoke(success) // Call callback function to check result
    }
}