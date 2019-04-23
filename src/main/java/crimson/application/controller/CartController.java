package crimson.application.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import crimson.application.dao.CartItemRepository;
import crimson.application.dao.CartRepository;
import crimson.application.dao.ProductRepository;
import crimson.application.dao.UserRepository;
import crimson.application.model.Cart;
import crimson.application.model.CartItem;
import crimson.application.model.Product;
import crimson.application.model.User;

@Controller
@RequestMapping("/user")
public class CartController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	@GetMapping("/addtocart/{id}")
	public String addtocart(@PathVariable("id") Long id,
			@RequestParam(name = "prod_quantity", required = false) Integer quantity, Model model, Principal principal,
			HttpSession session) {

		if (quantity == null) {
			quantity = 1;
		}

		User user = userRepository.findUserByEmail(principal.getName());
		Product product = productRepository.getOne(id);
		Cart cart = cartRepository.findCartByUser(user);
		if (cart == null) {
			cart = createCart(user, product, quantity);
		} else {
			modifyCart(cart, product, quantity);
		}
		cartRepository.save(cart);
		session.setAttribute("cart_count", cart.getQuantity());
		return "redirect:/products";
	}
	
	
	
	@GetMapping("/cart")
	public String cartDetails(Model model,Principal principal) {
		User user=userRepository.findUserByEmail(principal.getName());
		model.addAttribute("cart",cartRepository.findCartByUser(user));
		return "cart";
	}
	
	

	private Cart createCart(User user, Product product, Integer quantity) {
		Cart cart = new Cart();
		CartItem cartItem = createCartItem(product, quantity);
		cartItemRepository.save(cartItem);
		cartItem.setCart(cart);
		List<CartItem> cartItems = new ArrayList<CartItem>();
		cartItems.add(cartItem);
		cart.setCartItems(cartItems);
		cart.setQuantity(1);
		cart.setTotalAmount(product.getPrice().doubleValue());
		cart.setUser(user);
		return cart;
	}

	private CartItem createCartItem(Product product, Integer quantity) {
		CartItem cartItem = new CartItem();
		cartItem.setProduct(product);
		cartItem.setQuantity(quantity);
		cartItem.setUnitPrice(product.getPrice().doubleValue());
		cartItem.setTotalPrice(product.getPrice().doubleValue() * quantity);
		return cartItem;
	}

	private void modifyCart(Cart cart, Product product, Integer quantity) {
		CartItem cartItem = cartItemRepository.findCartItemByCartAndProduct(cart, product);
		if (cartItem == null) {
			cartItem = createCartItem(product, quantity);
			cartItem.setCart(cart);
		} else {
			modifyCartItem(cartItem, product, quantity);
		}
		cartItemRepository.save(cartItem);
		// cart.getCartItems().add(cartItem);
		cart.setQuantity(cart.getQuantity() + quantity);
		cart.setTotalAmount(cart.getTotalAmount() + (product.getPrice() * quantity));
	}

	private void modifyCartItem(CartItem cartItem, Product product, Integer quantity) {
		cartItem.setQuantity(cartItem.getQuantity() + quantity);
		cartItem.setTotalPrice(cartItem.getTotalPrice() + (product.getPrice() * quantity));
	}

}
