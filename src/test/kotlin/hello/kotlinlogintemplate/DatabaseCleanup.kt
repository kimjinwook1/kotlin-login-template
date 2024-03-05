package hello.kotlinlogintemplate

import com.google.common.base.CaseFormat
import jakarta.persistence.Entity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.Table
import jakarta.persistence.metamodel.EntityType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DatabaseCleanup {

    @PersistenceContext
    private val entityManager: EntityManager? = null
    private var tableNames: MutableList<String> = mutableListOf()

    fun afterPropertiesSet() {
        val entities = entityManager?.metamodel?.entities ?: emptySet<EntityType<*>>()
        tableNames = entities.filter { it.javaType.getAnnotation(Entity::class.java) != null && it.javaType.getAnnotation(Table::class.java) != null }
            .map { it.javaType.getAnnotation(Table::class.java).name }
            .toMutableList()

        val entityNames = entities.filter { it.javaType.getAnnotation(Entity::class.java) != null && it.javaType.getAnnotation(Table::class.java) == null }
            .map { CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, it.name) }

        tableNames.addAll(entityNames)
    }

    @Transactional
    fun execute() {
        entityManager?.flush()
        entityManager?.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE")?.executeUpdate()

        tableNames.forEach { tableName ->
            entityManager?.createNativeQuery("TRUNCATE TABLE $tableName")?.executeUpdate()
            entityManager?.createNativeQuery("ALTER TABLE $tableName ALTER COLUMN ID RESTART WITH 1")?.executeUpdate()
        }

        entityManager?.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE")?.executeUpdate()
    }

}
