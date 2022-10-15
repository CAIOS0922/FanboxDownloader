package client

import FanboxContract
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class FanboxApiClient(private val sessionId: String? = null): FanboxContract.ApiClient {
    private val httpClient = HttpClient(CIO) {
        defaultRequest {
            //header("accept", "application/json, text/plain")
            //header("accept-encoding", "gzip, deflate, br")
            //header("accept-language", "ja,en-US;q=0.9,en;q=0.8")
            header("origin", "https://www.fanbox.cc")
            header("referer", "https://www.fanbox.cc")
            header("user-agent",  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36")

            sessionId?.let { header("Cookie", "FANBOXSESSID=$it; privacy_policy_agreement=0; privacy_policy_notification=0") }
        }

        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                prettyPrint = true
                ignoreUnknownKeys = true
                coerceInputValues = true
                encodeDefaults = true
            })
        }
    }

    override suspend fun get(dir: String, parameters: Map<String, String>): HttpResponse {
        return httpClient.get {
            url(FanboxContract.BASE_URL + dir)

            for ((key, value) in parameters) {
                parameter(key, value)
            }
        }
    }

    override suspend fun download(url: String): HttpResponse {
        return httpClient.get {
            url(url)
        }
    }
}