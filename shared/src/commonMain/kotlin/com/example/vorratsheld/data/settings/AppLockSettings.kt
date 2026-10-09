package com.example.vorratsheld.data.settings

import com.example.vorratsheld.data.enums.LockTimeout
import com.example.vorratsheld.util.Sha256
import kotlin.random.Random

// App-Sperre: PIN wird nur als gesalzener Hash gespeichert, nie im Klartext
object AppLockSettings {
    const val PIN_LENGTH = 4

    private const val KEY_ENABLED = "app_lock_enabled"
    private const val KEY_PIN_HASH = "app_lock_pin_hash"
    private const val KEY_PIN_SALT = "app_lock_pin_salt"
    private const val KEY_BIOMETRICS = "app_lock_biometrics"
    private const val KEY_TIMEOUT = "app_lock_timeout"
    private const val HASH_ROUNDS = 10_000

    val enabled: Boolean
        get() = KeyValueStore.getString(KEY_ENABLED) == "true" && KeyValueStore.getString(KEY_PIN_HASH) != null

    var biometricsEnabled: Boolean
        get() = KeyValueStore.getString(KEY_BIOMETRICS) == "true"
        set(value) = KeyValueStore.putString(KEY_BIOMETRICS, value.toString())

    var timeout: LockTimeout
        get() = KeyValueStore.getString(KEY_TIMEOUT)
            ?.let { saved -> LockTimeout.entries.firstOrNull { it.name == saved } }
            ?: LockTimeout.ONE_MINUTE
        set(value) = KeyValueStore.putString(KEY_TIMEOUT, value.name)

    // Neuer PIN schaltet die Sperre gleich mit ein
    fun setPin(pin: String) {
        val salt = Random.nextBytes(16).toHex()
        KeyValueStore.putString(KEY_PIN_SALT, salt)
        KeyValueStore.putString(KEY_PIN_HASH, hashPin(pin, salt))
        KeyValueStore.putString(KEY_ENABLED, "true")
    }

    fun checkPin(pin: String): Boolean {
        val salt = KeyValueStore.getString(KEY_PIN_SALT) ?: return false
        val saved = KeyValueStore.getString(KEY_PIN_HASH) ?: return false
        return hashPin(pin, salt) == saved
    }

    // Beim Ausschalten, Abmelden oder "PIN vergessen"
    fun clear() {
        listOf(KEY_ENABLED, KEY_PIN_HASH, KEY_PIN_SALT, KEY_BIOMETRICS).forEach { KeyValueStore.putString(it, null) }
    }

    // Mehrere Runden, damit ein ausgelesener Hash nicht in Sekunden geknackt ist
    private fun hashPin(pin: String, salt: String): String {
        var bytes = (salt + pin).encodeToByteArray()
        repeat(HASH_ROUNDS) { bytes = Sha256.hash(bytes) }
        return bytes.toHex()
    }

    private fun ByteArray.toHex(): String =
        joinToString("") { (it.toInt() and 0xff).toString(16).padStart(2, '0') }
}
