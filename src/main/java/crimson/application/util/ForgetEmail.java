package crimson.application.util;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service("forgetemail")
public class ForgetEmail implements Email {

	@Autowired
	private JavaMailSender javaMailSender;

	@Override
	public boolean send(String to, String content, String contextPath) {
		try {
			javaMailSender.send(new MimeMessagePreparator() {
				@Override
				public void prepare(MimeMessage mimeMessage) throws Exception {
					MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
					messageHelper.addTo(to);
					messageHelper.setSubject("Reset Password");
					messageHelper.setText(
							"<p>We received a request to reset the password associated with this e-mail address. If you made this request, please follow the instructions below.</p>\r\n"
									+ "<p>Click the link below to reset your password using our secure server:</p>\r\n"
									+ "<a href='http://crimsontrading.in/resetpassword?email=" + to
									+ "'>Reset Password</a>\r\n" + "And Enter the code below:" + content + "\r\n"
									+ "<p>If you did not request to have your password reset, you can safely ignore this email. Rest assured your customer account is safe. </p>\r\n"
									+ "<p>If clicking the link doesn't seem to work, you can copy and paste the link into your browser's address window, or retype it there. Once you have returned to crimsontrading.in, we will give instructions for resetting your password.</p>\r\n"
									+ "\r\n" + "<p>Regards, </p>\r\n" + "<p>Aadvi Team</p>\r\n" + "",
							true);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
