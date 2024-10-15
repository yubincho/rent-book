package com.example.bookrent2.common.controller;


import com.example.bookrent2.rentBook.model.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {


    @GetMapping("/login")
    public String loginPage() {
        return "user/oauthLogin";
    }


    @GetMapping("/")
    public String mainPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isLoggedIn = authentication != null && authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String);

        System.out.println("Is logged in: " + isLoggedIn);

        model.addAttribute("isLoggedIn", isLoggedIn);

        if (isLoggedIn && authentication instanceof OAuth2AuthenticationToken) {
            OAuth2User oauth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
            String username = oauth2User.getAttribute("name");

            System.out.println("OAuth2User name: " + username);
            model.addAttribute("username", username);
        }

        return "main";
    }

}
