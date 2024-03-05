package hello.kotlinlogintemplate.member.domain

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import hello.kotlinlogintemplate.global.config.createQuery
import hello.kotlinlogintemplate.global.security.CustomUserDetails
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class MemberJdslRepository(
    private val entityManager: EntityManager,
){

    fun findAuthInfoById(id: Long): CustomUserDetails? {
        val query = jpql {
            selectNew<CustomUserDetails>(
                path(Member::id).`as`(expression(Long::class, "memberId")),
                path(Member::email),
                path(Member::role),
                path(Member::password)
            )
                .from(entity(Member::class))
                .where(path(Member::id).eq(id))
        }

        return entityManager.createQuery(query)
    }

    fun findAuthInfoByEmail(email: String): CustomUserDetails? {
        val query = jpql {
            selectNew<CustomUserDetails>(
                path(Member::id).`as`(expression(Long::class, "memberId")),
                path(Member::email),
                path(Member::role),
                path(Member::password)
            )
                .from(entity(Member::class))
                .where(path(Member::email).eq(email))
        }

        return entityManager.createQuery(query)
    }

}
