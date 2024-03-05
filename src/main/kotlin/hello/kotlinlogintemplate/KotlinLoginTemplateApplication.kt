package hello.kotlinlogintemplate

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinLoginTemplateApplication

fun main(args: Array<String>) {
    runApplication<KotlinLoginTemplateApplication>(*args)
}
