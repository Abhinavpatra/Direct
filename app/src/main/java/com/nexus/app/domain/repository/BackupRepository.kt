package com.nexus.app.domain.repository

import android.net.Uri
import com.nexus.app.core.result.AppResult

interface BackupRepository {
    suspend fun exportTo(uri: Uri): AppResult<Unit>
    suspend fun importFrom(uri: Uri): AppResult<Unit>
}
