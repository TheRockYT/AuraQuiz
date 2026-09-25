package one.felsen.auraquiz

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.schema.generator.json.jsonSchemaOf
import kotlinx.schema.json.encodeToString
import kotlinx.serialization.json.Json
import one.felsen.auraquiz.data.ExportFile
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExportFileJsonSchema {
    @Test
    fun generateJsonSchema() {
        val schema = jsonSchemaOf<ExportFile>()
        val schemaString = schema.encodeToString(Json { prettyPrint = true })
        println(schemaString)
    }
}
