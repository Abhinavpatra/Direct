package com.nexus.app.data.repository

import com.nexus.app.core.result.AppResult
import com.nexus.app.data.remote.ParsedTaskPayload
import com.nexus.app.domain.repository.AiTaskParserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HybridAiTaskParserRepository @Inject constructor(
    private val remoteParser: FirebaseAiTaskParserRepository,
    private val localParser: LocalAiTaskParserRepository
) : AiTaskParserRepository {
    override suspend fun parse(text: String): AppResult<ParsedTaskPayload> {
        val remoteResult = remoteParser.parse(text)
        
        return if (remoteResult is AppResult.Error) {
            // If remote fails (e.g. offline), try local
            val localResult = localParser.parse(text)
            if (localResult is AppResult.Success) {
                localResult
            } else {
                // Return original remote error if local also fails or isn't ready
                remoteResult
            }
        } else {
            remoteResult
        }
    }
}
