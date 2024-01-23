package com.gill.others.controller;

import com.gill.others.bean.Body;
import com.gill.web.api.Result;
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
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller
 *
 * @author gill
 * @version 2024/01/23
 **/
@org.springframework.stereotype.Controller
public class Controller {

    @ResponseBody
    @GetMapping("/str/{key}")
    public String str(@PathVariable("key") String key, @RequestParam("value") String value) {
        return key + "=" + value;
    }

    @ResponseBody
    @PostMapping("/res")
    public Result<Body> res(@RequestBody Body body) {
        return Result.success(body);
    }

    @ResponseBody
    @GetMapping("/noParam")
    public Result<String> noParam() {
        return Result.success();
    }

    @ResponseBody
    @GetMapping("/webEx")
    public Result<Object> webEx() {
        throw new WebException(HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @GetMapping("/ex")
    public Result<Object> ex() throws Exception {
        throw new Exception("error");
    }

    @ResponseBody
    @GetMapping("/validEx")
    public Result<String> validEx(@Valid @Min(0) @RequestParam("number") Integer number) {
        return Result.success();
    }
}
