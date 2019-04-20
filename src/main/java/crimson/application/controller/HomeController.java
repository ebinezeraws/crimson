package crimson.application.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import crimson.application.model.User;
import crimson.application.service.EmailService;



@Controller
public class HomeController {
	

	
	@GetMapping("/")
	public String indexPage(Model model,HttpServletRequest request) {
		model.addAttribute("user", new User());
		return "index";
	}
	

}
