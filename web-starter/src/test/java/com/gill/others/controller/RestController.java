package com.gill.others.controller;

import com.gill.others.bean.Body;
import com.gill.web.annotation.IgnoreAuth;
import com.gill.web.annotation.OperationPermission;
import com.gill.web.api.Response;
import com.gill.web.exception.WebException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller
 *
 * @author gill
 * @version 2024/01/23
 **/
@org.springframework.web.bind.annotation.RestController
@RequestMapping("rest")
public class RestController {

    @IgnoreAuth
    @GetMapping("/str/{key}")
    public Body str(@PathVariable("key") String key, @RequestParam("value") String value) {
        return new Body(key, value);
    }

    @IgnoreAuth
    @PostMapping("/res")
    public Response<Body> res(@RequestBody Body body) {
        return Response.success(body).build();
    }

    @IgnoreAuth
    @GetMapping("/noParam")
    public Response<String> noParam() {
        return Response.success().build();
    }

    @IgnoreAuth
    @GetMapping("/webEx")
    public Response<Object> webEx() {
        throw new WebException(HttpStatus.BAD_REQUEST, "failed");
    }

    @IgnoreAuth
    @GetMapping("/ex")
    public Response<Object> ex() throws Exception {
        throw new Exception("error");
    }

    @IgnoreAuth
    @GetMapping("/validEx")
    public Response<String> validEx(@Valid @Min(0) @RequestParam("number") Integer number) {
        return Response.success(String.valueOf(number)).build();
    }

    @OperationPermission(permissionExpression = "test")
    @GetMapping("/auth")
    public Response<String> auth() {
        return Response.success().build();
    }
}
