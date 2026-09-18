package com.example.core.diagnostics

import android.content.Context
import android.os.Build

data class AppDiagnostics(
    val versionName: String,
    val versionCode: Long,
    val device: String,
    val sdk: Int
)

object DiagnosticsHelper {
    fun getAppDiagnostics(context: Context): AppDiagnostics {
        val info = runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0)
        }.getOrNull()

        val vName = info?.versionName.orEmpty()
        val vCode = if (info != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                info.versionCode.toLong()
            }
        } else {
            0L
        }

        return AppDiagnostics(
            versionName = vName,
            versionCode = vCode,
            device = "${Build.MANUFACTURER} ${Build.MODEL}",
            sdk = Build.VERSION.SDK_INT
        )
    }
}
