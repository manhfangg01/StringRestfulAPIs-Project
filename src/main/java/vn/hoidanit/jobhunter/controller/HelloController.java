package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.Meter.Id;
import vn.hoidanit.jobhunter.domain.RestResponse;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class HelloController {

    @GetMapping("/")
    public String getHelloWorld() throws IdInvalidException {
        if (true)
            throw new IdInvalidException("Ban bi gay");
        return "Hello PVM";
    }

}
