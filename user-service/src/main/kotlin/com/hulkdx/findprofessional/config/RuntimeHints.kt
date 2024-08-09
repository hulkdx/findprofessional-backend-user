package com.hulkdx.findprofessional.config

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
