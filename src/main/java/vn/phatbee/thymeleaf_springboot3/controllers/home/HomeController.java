package vn.phatbee.thymeleaf_springboot3.controllers.home;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String index(){
        return "admin/categories";
    }
}
