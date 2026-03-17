package com.marvel.app.data.api

import com.marvel.app.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.security.MessageDigest
import javax.inject.Inject

/**
 * Adds required auth params to every Marvel API request.
 * Marvel API requires: ts, apikey, hash (md5 of ts+privateKey+publicKey)
 */
class MarvelAuthInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        val timestamp = System.currentTimeMillis().toString()
        val hash = generateHash(timestamp)

        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("ts", timestamp)
            .addQueryParameter("apikey", BuildConfig.MARVEL_PUBLIC_KEY)
            .addQueryParameter("hash", hash)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }

    private fun generateHash(timestamp: String): String {
        val input = timestamp + BuildConfig.MARVEL_PRIVATE_KEY + BuildConfig.MARVEL_PUBLIC_KEY
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
