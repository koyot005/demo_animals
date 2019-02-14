package app.repo

import org.springframework.data.jpa.repository.JpaRepository
import app.model.User

interface UserRepository : JpaRepository<User, Int> {

    fun existsByUsername(username: String?): Boolean

    fun findByUsername(username: String?): User?
}