package com.example.vscodeshop.controller;

import com.example.vscodeshop.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    private String getUsername(HttpSession session) {
        Object loginUser = session.getAttribute("loginUser");
        return loginUser == null ? null : loginUser.toString();
    }

    @GetMapping
    public String cartPage(HttpSession session, Model model) {
        String username = getUsername(session);

        if (username == null) {
            return "redirect:/login-page?cart=1";
        }

        model.addAttribute("cartItems", cartService.getCartItems(username));
        model.addAttribute("total", cartService.getTotal(username));
        model.addAttribute("totalQuantity", cartService.getTotalQuantity(username));

        return "cart";
    }

    @PostMapping("/add/{productId}")
    public String addToCart(
            @PathVariable Long productId,
            HttpSession session) {

        String username = getUsername(session);

        if (username == null) {
            session.setAttribute("requestedCartProductId", productId);
            return "redirect:/login-page?cart=1";
        }

        cartService.addProduct(username, productId);
        return "redirect:/cart";
    }

    @PostMapping("/increase/{cartItemId}")
    public String increase(
            @PathVariable Long cartItemId,
            HttpSession session) {

        String username = getUsername(session);

        if (username == null) {
            return "redirect:/login-page?cart=1";
        }

        cartService.increase(cartItemId, username);
        return "redirect:/cart";
    }

    @PostMapping("/decrease/{cartItemId}")
    public String decrease(
            @PathVariable Long cartItemId,
            HttpSession session) {

        String username = getUsername(session);

        if (username == null) {
            return "redirect:/login-page?cart=1";
        }

        cartService.decrease(cartItemId, username);
        return "redirect:/cart";
    }

    @PostMapping("/remove/{cartItemId}")
    public String remove(
            @PathVariable Long cartItemId,
            HttpSession session) {

        String username = getUsername(session);

        if (username == null) {
            return "redirect:/login-page?cart=1";
        }

        cartService.remove(cartItemId, username);
        return "redirect:/cart";
    }
}
