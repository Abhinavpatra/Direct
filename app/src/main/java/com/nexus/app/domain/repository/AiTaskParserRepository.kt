package com.nexus.app.domain.repository

import com.nexus.app.core.result.AppResult
import com.nexus.app.data.remote.ParsedTaskPayload

interface AiTaskParserRepository {
    suspend fun parse(text: String): AppResult<ParsedTaskPayload>
}
