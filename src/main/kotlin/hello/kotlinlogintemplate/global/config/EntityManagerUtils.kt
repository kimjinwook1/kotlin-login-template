package hello.kotlinlogintemplate.global.config

import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderedParams
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import jakarta.persistence.EntityManager
import jakarta.persistence.Query

object JpqlUtils {
    val jpqlRenderContext: JpqlRenderContext = JpqlRenderContext()
    val jpqlRenderer: JpqlRenderer = JpqlRenderer()
}

inline fun <reified T> EntityManager.createQuery(query: SelectQuery<*>): T? {
    val jpql = JpqlUtils.jpqlRenderer.render(query, JpqlUtils.jpqlRenderContext)
    return createQuery(jpql.query, T::class.java)
        .apply { setParams(this, jpql.params) }
        .resultList
        .firstOrNull()
}

inline fun <reified T> EntityManager.createQueryList(query: SelectQuery<*>): List<T> {
    val jpql = JpqlUtils.jpqlRenderer.render(query, JpqlUtils.jpqlRenderContext)
    return this.createQuery(jpql.query, T::class.java)
        .apply { setParams(this, jpql.params) }
        .resultList
}

inline fun <reified T> EntityManager.createQueryCount(query: SelectQuery<*>): Long {
    val jpql = JpqlUtils.jpqlRenderer.render(query, JpqlUtils.jpqlRenderContext)
    return this.createQuery(jpql.query, T::class.java)
        .apply { setParams(this, jpql.params) }
        .firstResult.toLong()
}

fun setParams(query: Query, params: JpqlRenderedParams) {
    params.forEach { (name, value) ->
        query.setParameter(name, value)
    }
}
