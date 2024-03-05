package hello.kotlinlogintemplate.aa

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class AController {

    @GetMapping("", "/")
    fun index(): String {
        return "index"
    }

    @GetMapping("/loginForm")
    fun loginForm(): String {
        return "loginForm"
    }

    @GetMapping("/joinForm")
    fun joinForm(): String {
        return "joinForm"
    }

    @GetMapping("/test")
    fun test(): String {
        return "test"
    }

    @ResponseBody
    @GetMapping("/test2")
    fun test2(): String {
        return "test2"
    }

}
