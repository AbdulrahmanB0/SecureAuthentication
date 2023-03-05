package com.practise.domain.model.templating

import freemarker.cache.FileTemplateLoader
import freemarker.template.Configuration
import io.ktor.server.freemarker.*
import java.io.File
import java.io.StringWriter

class FreeMarkerProcessorService {

    private val config = Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS).apply {
        templateLoader = FileTemplateLoader(File("src/main/resources/templates"))
    }

    fun process(content: FreeMarkerContent): String {
        val writer = StringWriter()
        config.getTemplate(content.template).process(content.model, writer)
        return writer.toString()
    }
}
