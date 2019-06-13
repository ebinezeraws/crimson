package crimson.application.controller;

import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import crimson.application.model.Cart;
import crimson.application.model.Product;
import crimson.application.model.User;
import crimson.application.repository.CartRepository;
import crimson.application.repository.ProductRepository;
import crimson.application.repository.UserRepository;
import crimson.application.util.Validation;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private Validation validation;

	@GetMapping("/")
	public String indexPage(Model model, @RequestParam(name = "login", required = false) String login,
			HttpServletRequest request, Principal principal, HttpSession session) {

		model.addAttribute("user", new User());

		if (principal != null) {
			User user = null;
			if (session.getAttribute("reg_user") == null) {
				user = userRepository.findUserByEmail(principal.getName());
			}
			if (user != null) {
				session.setAttribute("reg_user", user);
				if (user.getRole().equalsIgnoreCase("ROLE_OWNER")) {
					return "redirect:/owner";
				} else if (user.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
					return "redirect:/products";
				}
				Cart cart = cartRepository.findCartByUser(user);
				if (cart == null) {
					session.setAttribute("cart_count", 0);
				} else {
					session.setAttribute("cart_count", cart.getQuantity());
				}
			} else {
				return "redirect:/logout";
			}
		}

		if (login != null) {
			if (login.equalsIgnoreCase("error")) {
				return "redirect:/products?login=error";
			}
			return "redirect:/products?login";
		}

		return "redirect:/products";
	}

	@GetMapping("/products")
	public String getProducts(@RequestParam(name = "status", required = false) Boolean status,
			@RequestParam(name = "disable", required = false) Boolean disableStatus, Model model, Principal principal,
			HttpSession session) {

		model.addAttribute("user", new User());
		model.addAttribute("products", productRepository.findAllByStatusIsTrue());

		model.addAttribute("disable", disableStatus);

		if (session.getAttribute("reg_user") != null) {
			Cart cart = ((User) session.getAttribute("reg_user")).getCart();
			if (cart != null) {
				session.setAttribute("cart_count", cart.getQuantity());
				model.addAttribute("cart", cart);
			}

		}

		if (status != null) {
			model.addAttribute("status", status);
		}

		return "products";
	}

	@GetMapping("/prod_details/{id}")
	public String productDetails(@PathVariable("id") Long id, Model model) {
		model.addAttribute("user", new User());
		Product product = productRepository.getOne(id);
		if (product == null) {
			return "redirect:/products?status=false";
		} else {
			model.addAttribute("product", product);
			model.addAttribute("top10Products", productRepository.findTop10ByStatusIsTrueOrderById());
			return "productdetails";
		}
	}

	@GetMapping("/aboutus")
	public String aboutUs(Model model) {
		model.addAttribute("user", new User());
		return "index";
	}

	@GetMapping("/profile")
	public String profile(Model model, HttpSession session) {
		model.addAttribute("user", session.getAttribute("reg_user"));
		return "profile";
	}

	@PostMapping("/profile")
	public String profileUpdate(@Valid @ModelAttribute("user") User user, Errors errors, Model model,
			HttpSession session, HttpServletRequest request, Principal principal) {
		if (errors.hasErrors()) {
			return "profile";
		}

		Map<String, String> errorMessages = validation.userUpdationValidation(user);
		if (errorMessages.size() > 0) {
			model.addAttribute("user", session.getAttribute("reg_user"));
			model.addAttribute("errorMessages", errorMessages);
			return "profile";
		}
		System.out.println(user.getPassword());
		userRepository.save(user);
		// updateAuthentication.doLogin(user, request);
		session.setAttribute("reg_user", user);
		return "redirect:/profile";
	}

}
