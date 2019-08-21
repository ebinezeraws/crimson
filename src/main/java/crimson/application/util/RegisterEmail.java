package crimson.application.util;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import crimson.application.model.User;
import crimson.application.repository.UserRepository;

@Service("regemail")
public class RegisterEmail implements Email {

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private UserRepository userRepository;

	@Value("${admin.email}")
	private String adminEmail;

	@Override
	public boolean send(String toEmail, String content, String contextPath) {

		User user = userRepository.findUserByEmail(toEmail);

		try {
			javaMailSender.send(new MimeMessagePreparator() {

				@Override
				public void prepare(MimeMessage mimeMessage) throws Exception {
					MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
					messageHelper.setTo(new String[] {toEmail, adminEmail});
					messageHelper.setText("<img style='float:right' src='cid:myimage'>" + "<div id=”mail”>\r\n"
							+ "<h1>Welcome to Aadvi Trading<h1>\r\n" + "\r\n" + "<h5>Hi, " + user.getUsername()
							+ ",</h5>\r\n" + "\r\n" + "<h3>Welcome to Aadvi Trading!!!</h3>\r\n"
							+ " <p>Aadvi products are shipped to worldwide markets including India, Singapore, Europe, USA, Middle East and Africa.</p>\r\n"
							+ "		\r\n" + "\r\n"
							+ "<p>Happy Shopping… <h3We look forward to placing your first order soon. </h3></p>\r\n"
							+ "\r\n" + "<h4>Regards</h4>\r\n" + "<h5>Crimson Team</h5>\r\n" + "\r\n" + "</div>\r\n"
							+ "", true);
					messageHelper.addInline("myimage", new UrlResource(contextPath + "/resources/img/index/logo.png"));
					messageHelper.setSubject("Aadvi Registration");
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}

}
