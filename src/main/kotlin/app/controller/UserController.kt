package app.controller

import javax.servlet.http.HttpServletRequest

import org.springframework.beans.factory.annotation.Autowired

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import app.model.Token
import app.model.User
import app.service.UserService

@RestController
@RequestMapping("/users")
@Api(tags = ["users"])
class UserController(@Autowired private val userService: UserService) {

    @PostMapping("/signin")
    @ApiOperation(value = "\${UserController.signin}", response = Token::class)
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "signed in"),
        ApiResponse(code = 400, message = "Something went wrong"),
        ApiResponse(code = 422, message = "Invalid username/password supplied")
    ])

    fun signin(@RequestBody user: User, req: HttpServletRequest): Token {
        return userService.signin(user, req)
    }

    @PostMapping("/signup")
    @ApiOperation(value = "\${UserController.signup}", response = Token::class)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "user created"),
        ApiResponse(code = 500, message = "Something went wrong")
    ])
    fun signup(@ApiParam("Signup User") @RequestBody user: User): Token {
        return userService.signup(user)
    }

    @GetMapping(value = ["/me"])
    @ApiOperation(value = "\${UserController.me}", response = User::class)
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "ok"),
        ApiResponse(code = 404, message = "Not found"),
        ApiResponse(code = 500, message = "Something went wrong")
    ])

    fun whoami(req: HttpServletRequest): User {
        return userService.whoami(req)
    }

    @GetMapping("/refresh")
    fun refresh(req: HttpServletRequest): Token {
        return userService.refresh(req)
    }

}
