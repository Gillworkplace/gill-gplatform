package com.gill.others.controller;

import com.gill.others.bean.Body;
import com.gill.web.annotation.IgnoreAuth;
import com.gill.web.api.Response;
import com.gill.web.exception.WebException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller
 *
 * @author gill
 * @version 2024/01/23
 **/
@org.springframework.stereotype.Controller
public class Controller {

    @IgnoreAuth
    @ResponseBody
    @GetMapping("/str/{key}")
    public Body str(@PathVariable("key") String key, @RequestParam("value") String value) {
        return new Body(key, value);
    }

    @IgnoreAuth
    @ResponseBody
    @PostMapping("/res")
    public Response<Body> res(@RequestBody Body body) {
        return Response.success(body).build();
    }

    @IgnoreAuth
    @ResponseBody
    @GetMapping("/noParam")
    public Response<String> noParam() {
        return Response.success().build();
    }

    @IgnoreAuth
    @ResponseBody
    @GetMapping("/webEx")
    public Response<Object> webEx() {
        throw new WebException(HttpStatus.BAD_REQUEST, "failed");
    }

    @IgnoreAuth
    @ResponseBody
    @GetMapping("/ex")
    public Response<Object> ex() throws Exception {
        throw new Exception("error");
    }

    @IgnoreAuth
    @ResponseBody
    @GetMapping("/validEx")
    public Response<String> validEx(@Valid @Min(0) @RequestParam("number") Integer number) {
        return Response.success().build();
    }
}
