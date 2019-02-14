package app.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import app.exception.CustomException
import app.exception.Type
import app.model.SigninAttempt
import app.model.Token
import app.model.User
import app.repo.SigninAttemptRepository
import app.repo.UserRepository
import app.security.JwtProvider

import javax.servlet.http.HttpServletRequest
import java.util.*


@Service
class UserService(@Autowired private val userRepository: UserRepository,
                  @Autowired private val signinAttemptRepository: SigninAttemptRepository,
                  @Autowired private val passwordEncoder: PasswordEncoder,
                  @Autowired private val jwtProvider: JwtProvider,
                  @Autowired private val authenticationManager: AuthenticationManager,
                  @Value("\${security.signin.max-tries}") val maxTries: Int,
                  @Value("\${security.signin.ban-time}") val expireTime: Int
) {

    fun signin(user: User, req: HttpServletRequest): Token {
        try {

            val cal = Calendar.getInstance().also { it.time = Date() }
            cal.set(Calendar.MILLISECOND, (cal.get(Calendar.MILLISECOND) - expireTime))
            val counter = signinAttemptRepository.countAfter(cal.time, req.remoteAddr)
            if (counter >= maxTries) {
                throw CustomException(Type.TOO_MANY_SIGNIN_ATTEMPTS)
            }

            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(user.username, user.password))
            signinAttemptRepository.deleteByIp(req.remoteAddr)
            return Token(jwtProvider.createToken(user.username!!), jwtProvider.validityInMilliseconds)

        } catch (e: AuthenticationException) {
            val attempt = SigninAttempt().apply { creationDateTime = Date(); ip = req.remoteAddr }
            signinAttemptRepository.save(attempt)
            throw CustomException(Type.INVALID_USERNAME_PASSWORD)
        }

    }

    fun signup(user: User): Token {
        if (!userRepository.existsByUsername(user.username)) {
            user.password = passwordEncoder.encode(user.password)
            userRepository.save(user)
            return Token(jwtProvider.createToken(user.username!!), jwtProvider.validityInMilliseconds)
        } else {
            throw CustomException(Type.USERNAME_ALREADY_IN_USE)
        }
    }

    fun whoami(req: HttpServletRequest): User {
        return userRepository.findByUsername(req.remoteUser) ?: throw CustomException(Type.NOT_FOUND)
    }

    fun refresh(req: HttpServletRequest): Token {
        return Token(jwtProvider.createToken(req.remoteUser), jwtProvider.validityInMilliseconds)
    }

}
