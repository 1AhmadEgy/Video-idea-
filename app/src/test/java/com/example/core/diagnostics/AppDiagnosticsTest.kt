package com.example.core.diagnostics

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppDiagnosticsTest {

    @Test
    fun `getAppDiagnostics populates non-null system and app metadata without PII`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val diagnostics = DiagnosticsHelper.getAppDiagnostics(context)

        assertNotNull(diagnostics.device)
        assertNotNull(diagnostics.sdk)
        assertNotNull(diagnostics.versionName)
        assertEquals(context.packageManager.getPackageInfo(context.packageName, 0).versionName, diagnostics.versionName)
    }
}
