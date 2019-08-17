
package crimson.application.controller;

import java.security.Principal;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import crimson.application.model.Address;
import crimson.application.model.Cart;
import crimson.application.model.CartItem;
import crimson.application.model.Order;
import crimson.application.model.OrderItem;
import crimson.application.model.User;
import crimson.application.model.UserDetails;
import crimson.application.service.AddressService;
import crimson.application.service.CartService;
import crimson.application.service.OrderItemService;
import crimson.application.service.OrderService;
import crimson.application.util.CartUtil;
import crimson.application.util.OrderPlacedEmail;

@Controller

@RequestMapping("/user")
public class CheckoutController {

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderItemService orderItemService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private CartUtil cartUtilService;

	@Autowired

	@Qualifier("orderPlacedEmail")
	private OrderPlacedEmail orderPlacedEmail;

	@GetMapping("/checkout")
	public String checkoutPage(Model model, HttpSession session) {

		Cart cart = cartService.getCart((User) (session.getAttribute("reg_user")));

		if (cart.getQuantity() == 0) {
			return "redirect:/user/cart";
		}

		model.addAttribute("userDetails", cart.getUser().getUserDetails());
		model.addAttribute("cart", cart);
		return "checkout";
	}

	@PostMapping("/checkout")
	public String orderCheckout(@Valid @ModelAttribute("userDetails") UserDetails userDetails, Errors errors,
			Model model, Principal principal, HttpServletRequest request, HttpSession session) {

		Cart cart = cartService.getCart(((User) (session.getAttribute("reg_user"))));

		if (cart.getQuantity() == 0) {
			return "redirect:/user/cart";
		}

		if (errors.hasErrors()) {
			model.addAttribute("cart", cart);
			return "checkout";
		}

		System.err.println(userDetails);
		System.err.println(userDetails.getAddress());

		// address.setOrder(order);

		Order order = saveOrder(cart, userDetails);

		if (order == null) {
			model.addAttribute("cart", cart);
			model.addAttribute("order_status", false);
			return "checkout";
		}

		// order.setAddress(null);

		orderPlacedEmail.send(cart.getUser().getEmail(), order.getOrderId().toString(),
				"http://" + request.getServerName() + ":" + request.getServerPort());
		cartUtilService.resetCart(cart);
		session.setAttribute("cart_count", 0);
		return "redirect:/user/bill/" + order.getOrderId();
	}

	@GetMapping("/bill/{id}")
	private String billPage(@PathVariable("id") Long id, Model model) {
		Order order = orderService.get(id);
		model.addAttribute("order", order);
		return "billpage";
	}

	private Order saveOrder(Cart cart, UserDetails userDetails) {
		Order order = new Order();
		order.setQuantity(cart.getQuantity());
		order.setTotalAmount(cart.getTotalAmount());
		order.setUser(cart.getUser());
		order.setDeliveryStatus(false);
		order.setDispatchStatus(false);
		order.setOrderedDate(new Date());
		order.setPaymentStatus(false);

		Calendar cal = Calendar.getInstance();
		cal.setTime(order.getOrderedDate());
		cal.add(Calendar.DAY_OF_MONTH, 3);

		order.setDeliveryDate(cal.getTime());

		Address address = userDetails.getAddress();
		userDetails.setAddress(null);
		address.setId(null);
		address.setOrder(order);
		order.setOrderAddress(address);

		if (orderService.saveOrUpdate(order) == null) {
			return null;
		}
		for (CartItem cartItem : cart.getCartItems()) {
			saveOrderItem(cartItem, order);
		}

		return order;
	}

	private void saveOrderItem(CartItem cartItem, Order order) {
		OrderItem orderItem = new OrderItem();
		orderItem.setOrder(order);
		orderItem.setProduct(cartItem.getProduct());
		orderItem.setQuantity(cartItem.getQuantity());
		orderItem.setTotalPrice(cartItem.getTotalPrice());
		orderItem.setUnitPrice(cartItem.getUnitPrice());
		orderItemService.saveOrUpdate(orderItem);
	}
}
