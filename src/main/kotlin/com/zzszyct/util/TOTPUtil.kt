package com.zzszyct.util

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator
import java.time.Duration
import java.time.Instant
import javax.crypto.spec.SecretKeySpec

/**
 * TOTPUtil 用于生成 TOTP 秘钥以及验证码验证，
 * 秘钥以 Base32 格式保存，验证码采用 30 秒有效期的 TOTP 算法。
 */
object TOTPUtil {
    private const val DEFAULT_ALGORITHM = "HmacSHA1"
    private val totpGenerator = TimeBasedOneTimePasswordGenerator(Duration.ofSeconds(30))

    /**
     * 生成随机 Base32 秘钥（默认 16 个字符）
     */
    fun generateSecret(length: Int = 16): String {
        val base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        return (1..length).map { base32Chars.random() }.joinToString("")
    }

    /**
     * 内部：将 Base32 编码字符串解码为字节数组
     */
    private fun base32Decode(base32: String): ByteArray {
        val base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        val cleaned = base32.trim().replace("=", "").replace(" ", "").toUpperCase()
        val output = ByteArray((cleaned.length * 5) / 8)
        var buffer = 0
        var bitsLeft = 0
        var index = 0
        for (char in cleaned) {
            val value = base32Chars.indexOf(char)
            if (value < 0) continue
            buffer = (buffer shl 5) or value
            bitsLeft += 5
            if (bitsLeft >= 8) {
                output[index++] = ((buffer shr (bitsLeft - 8)) and 0xFF).toByte()
                bitsLeft -= 8
            }
        }
        return output
    }

    /**
     * 生成指定时刻的 TOTP 验证码
     *
     * @param secret Base32 秘钥
     * @param time 指定时间，默认当前时间
     * @return TOTP 验证码字符串
     */
    fun generateCurrentTotp(secret: String, time: Instant = Instant.now()): String {
        val keyBytes = base32Decode(secret)
        val key = SecretKeySpec(keyBytes, DEFAULT_ALGORITHM)
        return totpGenerator.generateOneTimePasswordString(key, time)
    }

    /**
     * 验证用户输入的 TOTP 验证码是否正确，
     * 允许当前时间以及前后各 30 秒的窗口范围
     *
     * @param secret Base32 秘钥
     * @param totp 用户输入的验证码
     * @return true 表示验证通过，false 表示失败
     */
    fun verify(secret: String, totp: String): Boolean {
        val now = Instant.now()
        val codes = listOf(
            generateCurrentTotp(secret, now.minusSeconds(30)),
            generateCurrentTotp(secret, now),
            generateCurrentTotp(secret, now.plusSeconds(30))
        )
        return totp in codes
    }
}
