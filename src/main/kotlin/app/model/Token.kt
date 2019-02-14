package app.model

data class Token(
        val token: String,
        val expiresIn: Long,
        val tokenType: String = "Bearer"
)