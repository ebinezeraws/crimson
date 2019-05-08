package crimson.application.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import crimson.application.model.User;
import crimson.application.repository.UserRepository;
import crimson.application.util.Email;
import crimson.application.util.RandomPasswordGenerator;
import crimson.application.util.Validation;

@Controller
public class SignupAndLoginController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	@Qualifier("forgetemail")
	private Email emailService;

	@Autowired
	private RandomPasswordGenerator randomPasswordGenerator;
	
	@Autowired
	@Qualifier("regemail")
	private Email regEmailService;

	@Autowired
	private Validation validation;

	@PostMapping("/register")
	public String userRegister(@ModelAttribute @Valid User user, Errors errors, Model model,
			HttpServletRequest request) {
		if (errors.hasErrors()) {
			model.addAttribute("signup_status", "input_errors");
			return "products";
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		Map<String, String> error_messages = validation.userExistenceValidation(user);

		if (error_messages.size() > 0) {
			model.addAttribute("signup_status", "input_errors");
			model.addAttribute("error_messages", error_messages);
			return "products";
		}
		
		userRepository.save(user);

		if(regEmailService.send(user.getEmail(),"","http://"+request.getServerName()+":"+request.getServerPort())) {
			
		}else {
			System.out.println("Register Error");
			model.addAttribute("signup_status", "input_errors");
			model.addAttribute("email_error", "Email is not valid. Enter correct email.");
			return "products";
		}
		
		
		return "redirect:/?login";
	}

	@GetMapping("/forgetpassword")
	public String forgetPassword(Model model, HttpServletRequest request) {
		model.addAttribute("user", new User());
		return "forgetpassword";
	}

	@PostMapping("/verifyemail")
	public String verifyemail(@RequestParam("email") String email, HttpServletRequest request, Model model) {

		User user = userRepository.findUserByEmail(email);

		if (user != null) {
			String password = randomPasswordGenerator.generatePassword();
			user.setPassword(passwordEncoder.encode(password));
			userRepository.save(user);
			emailService.send(user.getEmail(), password,
					"http://" + request.getServerName() + ":" + request.getServerPort());
			return "redirect:/resetpassword?email=" + user.getEmail();
		}
		model.addAttribute("user", new User());
		model.addAttribute("message", "Email is not register.");
		return "forgetpassword";
	}

	@GetMapping("/resetpassword")
	public String resetPassword(@RequestParam("email") String email, Model model) {
		model.addAttribute("email", email);
		model.addAttribute("user", new User());
		return "resetpassword";
	}

	@PostMapping("/resetpassword")
	public String resetNewPassword(@RequestParam("email") String email, @RequestParam("password") String password,
			Model model) {

		User user = userRepository.findUserByEmail(email);
		model.addAttribute("email", email);
		model.addAttribute("user", new User());
		if (passwordEncoder.matches(password, user.getPassword())) {
			return "redirect:/newpassword?email=" + email;
		}
		model.addAttribute("password_match", false);
		return "resetpassword";
	}

	@GetMapping("/newpassword")
	public String newPassword(@RequestParam("email") String email, Model model) {
		model.addAttribute("email", email);
		model.addAttribute("user", new User());
		return "newpassword";
	}

	@PostMapping("/newpassword")
	public String storeNewPassword(@RequestParam("email") String email, @RequestParam("password") String password,
			@RequestParam("confirmPassword") String confirmPassword, Model model) {

		if (password.equals(confirmPassword)) {
			User user = userRepository.findUserByEmail(email);
			user.setPassword(passwordEncoder.encode(password));
			userRepository.save(user);
			return "redirect:/?login";
		}
		model.addAttribute("user", new User());
		model.addAttribute("message", "Password and Confirm Password should be same");
		return "newpassword";
	}

}
