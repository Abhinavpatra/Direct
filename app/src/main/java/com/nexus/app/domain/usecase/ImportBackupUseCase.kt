package com.nexus.app.domain.usecase

import android.net.Uri
import com.nexus.app.domain.repository.BackupRepository
import javax.inject.Inject

class ImportBackupUseCase @Inject constructor(
    private val backupRepository: BackupRepository,
) {
    suspend operator fun invoke(uri: Uri) = backupRepository.importFrom(uri)
}
