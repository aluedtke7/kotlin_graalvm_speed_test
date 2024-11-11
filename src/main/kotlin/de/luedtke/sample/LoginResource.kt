package de.luedtke.sample

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType

@Path("/login")
class LoginResource {
    private val algorithm: Algorithm = Algorithm.HMAC256("secret")
    private val logger: Logger = LoggerFactory.getLogger(LoginResource::class.java)

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun postLogin(user: User): LoginResult {
        logger.info("${user.username} ${user.password}")
        val result = LoginResult()
        try {
            result.jwt = JWT.create()
                .withIssuer("auth0")
                .withClaim("user", user.username)
                .withClaim("sub", 1)
                .withClaim("admin", true)
                .withExpiresAt(Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant()))
                .withKeyId("0001")
                .sign(algorithm)
        } catch (exception: JWTCreationException) {
            //Invalid Signing configuration / Couldn't convert Claims.
        }
        return result
    }
}