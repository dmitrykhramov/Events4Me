package com.metropolia.events4me.Controller;

import com.metropolia.events4me.Model.User;
import com.metropolia.events4me.Service.UserService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class IndexController {

    private UserService userService;

    @Autowired
    @Qualifier("UserServiceImpl")
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    // Redirects to index page for login/sign-up
    @RequestMapping("/")
    public String home() {
        return "redirect:/login";
    }

    // Login page
    @RequestMapping("/login")
    public String index() {
        return "login";
    }

    // Sign-up initial page
    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signup(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "signup";
    }

    // Sign-up request
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signupPost(@ModelAttribute("user") User user, Model model) {

        if(userService.checkUserExists(user.getUsername(), user.getEmail()))  {
            if (userService.checkEmailExists(user.getEmail())) {
                model.addAttribute("emailExists", true);
            }
            if (userService.checkUsernameExists(user.getUsername())) {
                model.addAttribute("usernameExists", true);
            }
            return "signup";
        } else {
            userService.saveOrUpdateUser(user);
            return "redirect:/";
        }
    }

    // User home page after login
    @RequestMapping("/events4me")
    public String userFront(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userEvents", user.getEvents());
        model.addAttribute("userFriend", user.getFriends());
        //Other information...
        return "events4me";
    }

    //Users private profile (for testing) *REMEMBER TO MOVE TO CORRECT CONTROLLER*
    @RequestMapping("events4me/profile/{id}")
    public String getUser(@PathVariable Integer id, Model model){
        model.addAttribute("user", userService.getById(id));
        return "myprofile";
    }

    @RequestMapping("/events4me/discoverevents")
    public String discoverEvents(Principal principal, Model model){
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "discoverevents";
    }

}
