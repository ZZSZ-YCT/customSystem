package com.zzszyct.util

import de.mkammerer.argon2.Argon2Factory

/**
 * Argon2Util 用于对密码进行哈希和校验，使用 Argon2 算法。
 */
object Argon2Util {
    private val argon2 = Argon2Factory.create()

    /**
     * 对明文密码进行哈希处理
     *
     * @param password 明文密码
     * @return 哈希后的字符串
     */
    fun hash(password: String): String {
        // 参数说明：iterations=2, 内存=65536 KB, 并行度=1
        return argon2.hash(2, 65536, 1, password.toCharArray())
    }

    /**
     * 校验明文密码与存储的哈希是否匹配
     *
     * @param password 明文密码
     * @param hash 存储的哈希值
     * @return true 表示匹配，false 表示不匹配
     */
    fun verify(password: String, hash: String): Boolean {
        return argon2.verify(hash, password.toCharArray())
    }
}
