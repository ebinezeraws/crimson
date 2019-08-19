package crimson.application.controller;

import java.security.Principal;
import java.util.Arrays;
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
import crimson.application.model.UserDetails;
import crimson.application.repository.CartRepository;
import crimson.application.repository.ProductRepository;
import crimson.application.repository.UserRepository;
import crimson.application.service.CartService;
import crimson.application.service.CategoryService;
import crimson.application.service.ProductService;
import crimson.application.service.UserCategoryService;
import crimson.application.service.UserDetailsService;
import crimson.application.service.UserService;
import crimson.application.util.Validation;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private UserCategoryService userCategoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private CartService cartService;

	@Autowired
	private Validation validation;

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/")
	public String indexPage(Model model, @RequestParam(name = "login", required = false) String login,
			HttpServletRequest request, Principal principal, HttpSession session) {
		model.addAttribute("user", new User());
		if (principal != null) {
			model.addAttribute("categories", categoryService.getCategories());
			User user = null;
			user = userService.getUserByEmail(principal.getName());
			if (user != null) {
				session.setAttribute("reg_user", user);
				if (user.getRole().equalsIgnoreCase("ROLE_OWNER")) {
					return "redirect:/owner";
				} else if (user.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
					return "redirect:/products";
				}
				Cart cart = cartService.getCart(user);
				if (cart == null) {
					session.setAttribute("cart_count", 0);
				} else {
					session.setAttribute("cart_count", cart.getQuantity());
				}

				if (user.getUserDetails() == null) {
					return "redirect:/profile/" + user.getUserId() + "?redirect=true";
				}

				return "redirect:/categories";
			} else {
				System.out.println("logout");
				return "redirect:/logout";
			}
		}

		return "index";
	}

	@GetMapping("/categories")
	public String getCategories(Model model) {
		model.addAttribute("categories", categoryService.getCategories());
		model.addAttribute("categorymenu", "active");
		model.addAttribute("user", new User());
		return "categories";
	}

	@GetMapping("/products")
	public String getProducts(@RequestParam(name = "status", required = false) Boolean status,
			@RequestParam(name = "disable", required = false) Boolean disableStatus,
			@RequestParam(name = "cartExists", required = false) String cartStatus, Model model, Principal principal,
			HttpSession session) {

		model.addAttribute("user", new User());
		model.addAttribute("productsmenu", "active");
		model.addAttribute("disable", disableStatus);

		if (session.getAttribute("reg_user") != null) {
			User user = ((User) session.getAttribute("reg_user"));

			if (user.getRole().equals("ROLE_ADMIN")) {
				model.addAttribute("products", productService.getProducts(user.getAdminDetails().getCategory()));
			} else {
				model.addAttribute("products", productService.getActiveProducts());
				if (user.getRole().equals("ROLE_USER")) {
					model.addAttribute("cartStatus", cartStatus);
					Cart cart = userService.getUserByEmail(user.getEmail()).getCart();
					if (cart != null) {
						System.out.println("cart is existed");
						session.setAttribute("cart_count", cart.getQuantity());
						model.addAttribute("cart", cart);
					}
				}
			}

		}

		if (status != null) {
			model.addAttribute("status", status);
		}

		return "products";
	}

	@GetMapping("/catproducts/{id}")
	public String catProducts(@RequestParam(name = "status", required = false) Boolean status,
			@RequestParam(name = "disable", required = false) Boolean disableStatus,
			@RequestParam(name = "cartExists", required = false) String cartStatus, @PathVariable("id") Long categoryId,
			Model model, Principal principal, HttpSession session) {

		model.addAttribute("user", new User());
		model.addAttribute("products", productService.getProducts(categoryService.getCategory(categoryId)));
		model.addAttribute("productsmenu", "active");
		model.addAttribute("cartStatus", cartStatus);
		model.addAttribute("disable", disableStatus);

		if (session.getAttribute("reg_user") != null) {
			Cart cart = userService.getUserByEmail((((User) session.getAttribute("reg_user")).getEmail())).getCart();
			if (cart != null) {
				System.out.println("cart is existed");
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
		model.addAttribute("productsmenu", "active");
		Product product = productService.getProduct(id);
		if (product == null) {
			return "redirect:/products?status=false";
		} else {
			model.addAttribute("product", product);
			model.addAttribute("top10Products", productService.top10Products());
			return "productdetails";
		}
	}

	@GetMapping("/aboutus")
	public String aboutUs(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("aboutusmenu", "active");
		return "index";
	}

	@GetMapping("/profile")
	public String profile(Model model, HttpSession session) {
		model.addAttribute("user", session.getAttribute("reg_user"));
		model.addAttribute("userCategories", userCategoryService.getUserCategories());
		return "profile";
	}

	@GetMapping("/profile/{id}")
	public String profile(@PathVariable("id") Long id,
			@RequestParam(name = "redirect", required = false) String redirect, Model model, HttpSession session) {
		model.addAttribute("user", userService.getUserById(id));
		model.addAttribute("userCategories", userCategoryService.getUserCategories());

		if (redirect != null) {
			model.addAttribute("redirect", redirect);
		}

		return "profile";
	}

	@PostMapping("/profile")
	public String profileUpdate(@Valid @ModelAttribute("user") User user, Errors errors,
			@RequestParam(name = "redirect", required = false) String redirect, Model model, HttpSession session,
			HttpServletRequest request, Principal principal) {

		System.err.println(user);

		if (errors.hasErrors()) {
			System.err.println("errors");
			return "profile";
		}

		Map<String, String> errorMessages = validation.userUpdationValidation(user);
		if (errorMessages.size() > 0) {
			System.err.println("message errors");
			model.addAttribute("user", user);
			model.addAttribute("userCategories", userCategoryService.getUserCategories());
			model.addAttribute("errorMessages", errorMessages);
			model.addAttribute("redirect", redirect);
			return "profile";
		}

		UserDetails userDetails = user.getUserDetails();
		userDetails.setUser(user);

		userService.update(user);
		// updateAuthentication.doLogin(user, request);

		if (session.getAttribute("reg_user") != null) {
			session.setAttribute("reg_user", user);
			if (redirect != null) {
				System.out.println("redirect");
				System.out.println(principal);
				return "redirect:/";
			}
			return "redirect:/profile";
		} else {
			return "redirect:/?login";
		}
	}

	@GetMapping("/terms")
	public String termsAndCondtions() {
		return "termsandconditions";
	}

	@GetMapping("/privacy")
	public String privacy() {
		return "privacyandpolicy";
	}

}
