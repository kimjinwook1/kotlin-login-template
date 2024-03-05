package hello.kotlinlogintemplate.global.oauth

data class OAuth2Attribute(
    val attributes: Map<String, Any>,
    val attributeKey: String,
    val email: String,
    val provider: String,
    val providerId: String,
) {
    companion object {
        fun of(provider: String, attributeKey: String, attributes: Map<String, Any>): OAuth2Attribute {
            return when (provider) {
                "google" -> ofGoogle(provider, attributeKey, attributes)
                "kakao" -> ofKakao(provider, "email", attributes)
                "naver" -> ofNaver(provider, "id", attributes)
                else -> throw IllegalArgumentException("Unknown provider: $provider")
            }
        }

        private fun ofGoogle(provider: String, attributeKey: String, attributes: Map<String, Any>): OAuth2Attribute {
            val providerId = attributes["sub"] as String
            val email = attributes["email"] as String
            return OAuth2Attribute(attributes, attributeKey, email, provider, providerId)
        }

        private fun ofKakao(provider: String, attributeKey: String, attributes: Map<String, Any>): OAuth2Attribute {
            val kakaoAccount = attributes["kakao_account"] as Map<String, Any>
            val providerId = (attributes["id"] as Long).toString()
            val email = kakaoAccount["email"] as String
            return OAuth2Attribute(kakaoAccount, attributeKey, email, provider, providerId)
        }

        private fun ofNaver(provider: String, attributeKey: String, attributes: Map<String, Any>): OAuth2Attribute {
            val response = attributes["response"] as Map<String, Any>
            val providerId = response["id"].toString()
            val email = response["email"] as String
            return OAuth2Attribute(response, attributeKey, email, provider, providerId)
        }
    }

    fun convertToMap(): Map<String, String> {
        return mapOf(
            "id" to attributeKey,
            "key" to attributeKey,
            "email" to email,
            "provider" to provider,
            "providerId" to providerId,
        )
    }
}
