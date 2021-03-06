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
import org.springframework.web.bind.annotation.ResponseBody;

import crimson.application.model.Cart;
import crimson.application.model.CartItem;
import crimson.application.model.Product;
import crimson.application.model.User;
import crimson.application.repository.CartItemRepository;
import crimson.application.repository.CartRepository;
import crimson.application.service.CartItemService;
import crimson.application.service.CartService;
import crimson.application.service.ProductService;
import crimson.application.util.CartUtil;

@Controller
@RequestMapping("/user")
public class CartController {

	@Autowired
	private ProductService productService;

	@Autowired
	private CartService cartService;

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private CartUtil cartUtilService;

	@GetMapping("/addtocart/{id}")
	public String addtocart(@PathVariable("id") Long id,
			@RequestParam(name = "prod_quantity", required = false) Integer quantity, Model model, Principal principal,
			HttpSession session) {

		Integer qant = quantity;

		if (quantity == null) {
			quantity = 1;
		}

		User user = (User) session.getAttribute("reg_user");
		Product product = productService.getProduct(id);
		if (product == null) {
			return "redirect:/products";
		}
		Cart cart = cartService.getCart(user);

		if (cart == null) {
			cart = createCart(user, product, quantity);
		} else {
			modifyCart(cart, product, quantity);
		}

		if (cartService.saveOrUpdate(cart) == null) {
			return "redirect:/products";
		}
		session.setAttribute("cart_count", cart.getQuantity());

		if (qant == null) {
			return "redirect:/products";
		} else {
			return "redirect:/prod_details/" + id;
		}
	}

	@GetMapping("/cart")
	public String cartDetails(Model model, Principal principal, HttpSession session) {
		User user = (User) session.getAttribute("reg_user");
		
		Cart cart = cartService.getCart(user);

		if (cart == null) {
			return "redirect:/products?cartExists=false";
		}
        model.addAttribute("cartmenu", "active");
		model.addAttribute("cart", cart);
		model.addAttribute("cartItems", cartItemService.getCartItems(cart));
		session.setAttribute("cart_count", cart.getQuantity());
		return "cart";
	}

	@GetMapping("/clearcart")
	public String clearcart(HttpSession session) {
		User user = (User) session.getAttribute("reg_user");
		Cart cart = user.getCart();
		cartUtilService.resetCart(cart);
		return "redirect:/user/cart";
	}

	@GetMapping("/addcartitem/{id}")
	@ResponseBody
	public Boolean addUpdateCart(@PathVariable("id") Long id, Model model, HttpSession session) {

		CartItem cartItem = cartItemService.get(id);
		session.setAttribute("cart_count", (Integer.parseInt(session.getAttribute("cart_count").toString()) + 1));
		return increaseCartItemQuantity(cartItem, 1);
	}

	@GetMapping("/subcartitem/{id}")
	@ResponseBody
	public Boolean subUpdateCart(@PathVariable("id") Long id, Model model, HttpSession session) {
		CartItem cartItem = cartItemService.get(id);
		session.setAttribute("cart_count", (Integer.parseInt(session.getAttribute("cart_count").toString()) - 1));
		return decreaseCartItemQuantity(cartItem, 1);
	}

	@GetMapping("/checkcartitem/{id}")
	@ResponseBody
	public CartItem cartItemExists(@PathVariable("id") Long productId, HttpSession session) {
		User user = (User) session.getAttribute("reg_user");
		
		Cart cart = cartService.getCart(user);
		
		CartItem cartItem = cartItemService.getCartItem(cart, productService.getProduct(productId));
		return cartItem;
	}

	@GetMapping("/udpate_cartitem/{productId}/{quantity}")
	@ResponseBody
	public Integer updateCartItem(@PathVariable("productId") Long productId, @PathVariable("quantity") Integer quantity,
			Principal principal, HttpSession session) {

		User user = (User) session.getAttribute("reg_user");
		CartItem cartItem = cartItemService.getCartItem(user.getCart(), productService.getProduct(productId));

		int cart_quantity = Integer.valueOf(session.getAttribute("cart_count").toString());
		if (quantity > cartItem.getQuantity()) {
			session.setAttribute("cart_count", cart_quantity + (quantity - cartItem.getQuantity()));
			increaseCartItemQuantity(cartItem, quantity - cartItem.getQuantity());
		} else {
			session.setAttribute("cart_count", cart_quantity - (cartItem.getQuantity() - quantity));
			decreaseCartItemQuantity(cartItem, cartItem.getQuantity() - quantity);
		}

		return Integer.valueOf(session.getAttribute("cart_count").toString());
	}

	private Cart createCart(User user, Product product, Integer quantity) {
		Cart cart = new Cart();
		CartItem cartItem = createCartItem(product, quantity);
		if (cartItemService.saveOrUpdate(cartItem) == null) {
			return null;
		}

		cartItem.setCart(cart);
		List<CartItem> cartItems = new ArrayList<CartItem>();
		cartItems.add(cartItem);
		cart.setCartItems(cartItems);
		cart.setQuantity(quantity);
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
		CartItem cartItem = cartItemService.getCartItem(cart, product);
		if (cartItem == null) {
			cartItem = createCartItem(product, quantity);
			cartItem.setCart(cart);
		} else {
			modifyCartItem(cartItem, product, quantity);
		}
		cartItemService.saveOrUpdate(cartItem);
		// cart.getCartItems().add(cartItem);
		cart.setQuantity(cart.getQuantity() + quantity);
		cart.setTotalAmount(cart.getTotalAmount() + (product.getPrice() * quantity));
	}

	private void modifyCartItem(CartItem cartItem, Product product, Integer quantity) {
		cartItem.setQuantity(cartItem.getQuantity() + quantity);
		cartItem.setTotalPrice(cartItem.getTotalPrice() + (product.getPrice() * quantity));
	}

	private Boolean increaseCartItemQuantity(CartItem cartItem, Integer quantity) {

		Cart cart = cartItem.getCart();
		cartItem.setQuantity(cartItem.getQuantity() + quantity);
		cartItem.setTotalPrice(cartItem.getTotalPrice() + (cartItem.getUnitPrice() * quantity));
		if (cartItemService.saveOrUpdate(cartItem) == null) {
			return false;
		}
		cart.setQuantity(cart.getQuantity() + quantity);
		cart.setTotalAmount(cart.getTotalAmount() + (cartItem.getUnitPrice() * quantity));
		if (cartService.saveOrUpdate(cart) == null) {
			return false;
		}
		return true;
	}

	private Boolean decreaseCartItemQuantity(CartItem cartItem, Integer quantity) {

		Cart cart = cartItem.getCart();
		cart.setTotalAmount(cart.getTotalAmount() - (cartItem.getUnitPrice() * quantity));
		cart.setQuantity(cart.getQuantity() - quantity);
		if (cartService.saveOrUpdate(cart) == null) {
			return false;
		}
		if (cartItem.getQuantity() == 1) {
			cartItemService.delete(cartItem);
		} else {
			cartItem.setQuantity(cartItem.getQuantity() - quantity);
			cartItem.setTotalPrice(cartItem.getTotalPrice() - (cartItem.getUnitPrice() * quantity));
			if (cartItemService.saveOrUpdate(cartItem) == null) {
				return false;
			}
		}
		return true;
	}

}
