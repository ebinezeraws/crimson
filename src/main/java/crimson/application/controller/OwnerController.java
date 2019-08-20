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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import crimson.application.model.User;
import crimson.application.service.CategoryService;
import crimson.application.service.UserService;
import crimson.application.util.Email;
import crimson.application.util.RandomPasswordGenerator;
import crimson.application.util.Validation;

@Controller
@RequestMapping("/owner")
public class OwnerController {

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RandomPasswordGenerator randomPasswordGenerator;

	@Autowired
	private Validation validation;

	@Autowired
	@Qualifier("adminRegEmail")
	private Email emailService;

	@GetMapping
	public String ownerpage(@RequestParam(value = "status", required = false) String registerStatus, Model model) {

		if (registerStatus != null) {
			model.addAttribute("regStatus", registerStatus);
		}

		model.addAttribute("user", new User());
		model.addAttribute("categories", categoryService.getCategories());
		return "ownerpage";
	}

	@PostMapping("/registeradmin")
	public String addAdmin(@Valid @ModelAttribute("user") User user, Errors errors, Model model,
			HttpServletRequest request) {
		String password = randomPasswordGenerator.generatePassword();
		user.setPassword(passwordEncoder.encode(password));
		if (errors.hasErrors()) {
			model.addAttribute("categories", categoryService.getCategories());
			return "ownerpage";
		} else {
			Map<String, String> error_Messages = validation.userExistenceValidation(user);
			if (error_Messages.size() > 0) {
				model.addAttribute("error_messages", error_Messages);
				return "ownerpage";
			}
		}

		user.getAdminDetails().setUser(user);

		if (userService.saveOrUpdate(user) == null) {
			return "redirect:/owner?status=false";
		}
		emailService.send(user.getEmail(), password,
				"http://" + request.getServerName() + ":" + request.getServerPort());
		return "redirect:/owner?status=true";
	}

	@GetMapping("/admins")
	public String admins(@RequestParam(value = "id", required = false) String id, Model model) {

		if (id != null) {
			model.addAttribute("id", id);
		}
		model.addAttribute("adminlist", "active");
		model.addAttribute("user", new User());
		model.addAttribute("admins", userService.getAllUserByRole("ROLE_ADMIN"));
		return "adminlist";
	}

	@GetMapping("/disableadmin/{userId}")
	public String adminDisable(@PathVariable("userId") Long userId, Model model) {
		User user = userService.getUserById(userId);
		if (user != null) {
			user.setIsActive(false);
			userService.saveOrUpdate(user);
		} else {
			model.addAttribute("error_message", "User is not existed");
			return "adminlist";
		}

		return "redirect:/owner/admins";

	}

	@GetMapping("/activateadmin/{userId}")
	public String adminActivate(@PathVariable("userId") Long userId, Model model) {
		User user = userService.getUserById(userId);
		if (user != null) {
			user.setIsActive(true);
			userService.saveOrUpdate(user);
		} else {
			model.addAttribute("error_message", "User is not existed");
			return "adminlist";
		}

		return "redirect:/owner/admins";

	}

	@GetMapping("/editadmin/{userId}")
	public String editAdminDetails(@PathVariable("userId") Long userId, Model model) {
		User user = userService.getUserById(userId);
		if (user == null) {
			return "redirect:/owner/admins?id=false";
		} else {
			model.addAttribute("user", user);
			return "editadmin";
		}
	}

	@PostMapping("/update")
	public String updateAdminDetails(@Valid User user, Errors errors, Model model) {
		if (errors.hasErrors()) {
			return "editadmin";
		}

		Map<String, String> error_messages = validation.userUpdationValidation(user);

		if (error_messages.size() > 0) {
			model.addAttribute("error_messages", error_messages);
			return "editadmin";
		}

		userService.saveOrUpdate(user);

		return "redirect:/owner/admins";

	}

	@GetMapping("/users")
	public String users(Model model) {
		model.addAttribute("userslist", "active");
		model.addAttribute("user", new User());
		model.addAttribute("users", userService.getAllUserByRole("ROLE_USER"));
		return "userslist";
	}
}
