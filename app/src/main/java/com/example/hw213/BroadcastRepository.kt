package com.example.hw213

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import java.util.UUID
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class BroadcastEvent(val id: String, val type: String, val timestamp: String, val details: String)

class BroadcastRepository(context: Context) {
    private val appContext = context.applicationContext
    private val timeFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS", Locale.getDefault())


    fun observeEvents(): Flow<BroadcastEvent> = callbackFlow{
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent == null) return

                if (isInitialStickyBroadcast) return

                val event = createEvent(intent) ?: return

                trySend(event)
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_BATTERY_OKAY)
            addAction(AudioManager.ACTION_HEADSET_PLUG)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }

        ContextCompat.registerReceiver(
            appContext,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        awaitClose {
            appContext.unregisterReceiver(receiver)
        }


    }

    private fun createEvent(intent: Intent): BroadcastEvent?{
        val action = intent.action ?: return null
        val (type, description) = when (action) {

            Intent.ACTION_POWER_CONNECTED ->
                "Зарядний пристрій підключено" to
                        "Пристрій під'єднано до джерела живлення."

            Intent.ACTION_POWER_DISCONNECTED ->
                "Зарядний пристрій відключено" to
                        "Пристрій від'єднано від джерела живлення."

            Intent.ACTION_AIRPLANE_MODE_CHANGED -> {
                val isEnabled = intent.getBooleanExtra("state", false)
                "Режим «У літаку» змінено" to
                        if (isEnabled) "Режим «У літаку» увімкнено."
                        else "Режим «У літаку» вимкнено."
            }

            Intent.ACTION_BATTERY_LOW ->
                "Критичний рівень заряду" to
                        "Заряд батареї досяг критично низького рівня."

            Intent.ACTION_BATTERY_OKAY ->
                "Заряд відновлено" to
                        "Рівень заряду батареї повернувся до норми."

            AudioManager.ACTION_HEADSET_PLUG -> {
                val isPlugged = intent.getIntExtra("state", -1) == 1
                val hasMic = intent.getIntExtra("microphone", 0) == 1
                if (isPlugged) {
                    "Навушники підключено" to
                            if (hasMic) "Підключено дротові навушники з мікрофоном."
                            else "Підключено дротові навушники без мікрофона."
                } else {
                    "Навушники відключено" to "Дротові навушники від'єднано."
                }
            }

            Intent.ACTION_SCREEN_ON ->
                "Екран увімкнено" to "Дисплей пристрою увімкнено."

            Intent.ACTION_SCREEN_OFF ->
                "Екран вимкнено" to "Дисплей пристрою вимкнено або заблоковано."

            else ->
                "Невідома подія" to "Отримано подію без окремої обробки."
        }

        val details = buildString {
            appendLine(description)
            appendLine()
            appendLine("Action: $action")
            appendLine()
            appendLine("Додаткові параметри (Extras):")
            append(formatExtras(intent.extras))
        }

        return BroadcastEvent(
            id = UUID.randomUUID().toString(),
            type = type,
            timestamp = timeFormatter.format(Date()),
            details = details
        )

    }

    private fun formatExtras(extras: Bundle?): String {
        if (extras == null || extras.isEmpty) return "відсутні"

        return extras.keySet().sorted().joinToString(separator = "\n") { key ->
            @Suppress("DEPRECATION")
            "• $key = ${extras.get(key)}"
        }
    }

}