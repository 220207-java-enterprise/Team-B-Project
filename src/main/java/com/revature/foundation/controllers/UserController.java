package com.revature.foundation.controllers;

import com.revature.foundation.dtos.requests.ApproveUserRequest;
import com.revature.foundation.dtos.requests.DeleteRequest;
import com.revature.foundation.dtos.requests.LoginRequest;
import com.revature.foundation.dtos.requests.NewUserRequest;
import com.revature.foundation.dtos.responses.AppUserResponse;
import com.revature.foundation.models.AppUser;
import com.revature.foundation.services.UserService;
import com.revature.foundation.util.exceptions.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<AppUserResponse> getAllUsers(HttpServletRequest request, HttpServletResponse response)  {
        String token = request.getHeader("Authorization");
        if (token == null) {
            response.setStatus(401);
            return null;
        }

        return userService.getAllUsers(token, response);
    }

    @GetMapping("inactive")
    public List<AppUserResponse> getAllInactiveUsers(HttpServletRequest request, HttpServletResponse response)  {
        String token = request.getHeader("Authorization");
        if (token == null) {
            response.setStatus(401);
            return null;
        }

        return userService.getAllInactiveUsers(token, response);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)//accepted because admin needs to approve request
    @PostMapping
    public void registerNewUser(@RequestBody NewUserRequest request) {
        userService.register(request);
    }

    @PostMapping("login")
    public void login(@RequestBody LoginRequest request, HttpServletResponse response) {
        String token = userService.login(request);
        response.setHeader("Authorization", token);
    }

    @PutMapping(value = "approve")
    public void approveUser(@RequestBody ApproveUserRequest approveRequest, HttpServletRequest httpRequest, HttpServletResponse response) {
        String token = httpRequest.getHeader("Authorization");
        if (token == null) {
            response.setStatus(401);
            return;
        }

        response.setStatus(201);
        userService.approve(token, approveRequest, response);
    }

    @DeleteMapping()
    public void delteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request, HttpServletResponse response){
        String token = request.getHeader("Authorization");
        if (token == null) {
            response.setStatus(401);
            return;
        }
        response.setStatus(204);
        userService.delete(token, deleteRequest, response);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HashMap<String, Object> handleExceptions(RuntimeException e) {
        HashMap<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", e.getClass().getSimpleName() + ": " + e.getMessage());
        responseBody.put("timestamp", LocalDateTime.now());
        return responseBody;
    }
}
