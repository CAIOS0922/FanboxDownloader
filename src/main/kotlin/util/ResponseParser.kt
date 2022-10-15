package util

import io.ktor.client.call.*
import io.ktor.client.statement.*

suspend inline fun <reified T> HttpResponse.parse(allowRange: IntRange = 200..299, f: ((T?) -> (Unit)) = {}): T? {
    return (if(this.status.value in allowRange) this.body<T>() else null).also(f)
}