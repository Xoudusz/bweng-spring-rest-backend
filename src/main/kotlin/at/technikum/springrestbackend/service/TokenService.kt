package at.technikum.springrestbackend.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Service
class TokenService(
    @Value("\${jwt.secret}") private val secret: String = ""
) {
    private val signingKey: SecretKeySpec
        get() {
            val keyBytes: ByteArray = Base64.getDecoder().decode(secret)
            return SecretKeySpec(keyBytes, 0, keyBytes.size, "HmacSHA256")
        }

    fun generateToken(subject: String, role: String, expiration: Date, additionalClaims: Map<String, Any> = emptyMap()): String {
        val claims = additionalClaims.toMutableMap()
        claims["role"] = role // Add the role to the claims map

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(expiration)
            .signWith(signingKey)
            .compact()
    }




    fun extractUsername(token: String): String {
        return extractAllClaims(token).subject
    }

    fun extractRole(token: String): String? {
        return extractAllClaims(token)["role"] as? String
    }

    fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token)
            .body
    }
}