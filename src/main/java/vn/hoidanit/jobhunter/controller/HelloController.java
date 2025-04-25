package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class HelloController {

    @GetMapping("/")
    public String getHelloWorld() throws IdInvalidException {

        return "Hello PVM";
    }

}
