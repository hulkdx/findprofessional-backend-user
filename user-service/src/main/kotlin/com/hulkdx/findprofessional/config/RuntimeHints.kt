package com.hulkdx.findprofessional.config

import com.hulkdx.findprofessional.config.converter.InstantToOffsetDateTimeConverter
import com.hulkdx.findprofessional.config.converter.OffsetDateTimeToInstantConverter
import com.hulkdx.findprofessional.model.ApiError
import com.hulkdx.findprofessional.model.response.AuthResponse
import com.hulkdx.findprofessional.model.response.ProfessionalResponse
import com.hulkdx.findprofessional.model.response.TokenResponse
import com.hulkdx.findprofessional.model.response.UserResponse
import com.hulkdx.findprofessional.model.response.UserResponseType
import liquibase.changelog.ChangeLogHistoryServiceFactory
import liquibase.changelog.FastCheckService
import liquibase.changelog.visitor.ValidatingVisitorGeneratorFactory
import liquibase.database.LiquibaseTableNamesFactory
import liquibase.parser.SqlParserFactory
import liquibase.report.ShowSummaryGeneratorFactory
import liquibase.ui.LoggerUIService
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.jdbc.datasource.SimpleDriverDataSource

class RuntimeHints : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        listOf(
            ArrayList::class.java,
            SimpleDriverDataSource::class.java,
            OffsetDateTimeToInstantConverter::class.java,
            InstantToOffsetDateTimeConverter::class.java,
            ApiError::class.java,
            AuthResponse::class.java,
            TokenResponse::class.java,
            UserResponseType::class.java,
            UserResponse::class.java,
            ProfessionalResponse::class.java,
        ).forEach {
            hints.reflection().registerType(it, *MemberCategory.values())
        }
        hints.resources().registerPattern("db/changelog/db.changelog-master.sql")

        // Might be fixed in
        // https://github.com/oracle/graalvm-reachability-metadata/issues/431
        listOf(
            LoggerUIService::class.java,
            ChangeLogHistoryServiceFactory::class.java,
            FastCheckService::class.java,
            LiquibaseTableNamesFactory::class.java,
            ValidatingVisitorGeneratorFactory::class.java,
            ShowSummaryGeneratorFactory::class.java,
            SqlParserFactory::class.java,
        ).forEach {
            hints.reflection().registerType(it, *MemberCategory.values())
        }
    }
}
