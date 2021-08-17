package br.com.selat.kinesispoc.controller;

import br.com.selat.kinesispoc.contract.ServiceValidationException;
import br.com.selat.kinesispoc.contract.User;
import br.com.selat.kinesispoc.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

@RestController
@RequestMapping("user")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    private User save(@RequestBody User input){
        return userService.save(input);
    }
}
