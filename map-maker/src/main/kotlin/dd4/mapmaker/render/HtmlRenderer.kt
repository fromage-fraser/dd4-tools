package dd4.mapmaker.render

import dd4.mapmaker.MapMaker
import dd4.mapmaker.model.AreaMap
import dd4.mapmaker.model.MapIndexItem
import freemarker.template.Configuration
import java.io.FileWriter
import java.nio.file.Paths
import java.time.ZonedDateTime

class HtmlRenderer {

    private val templateConfig: Configuration = Configuration(Configuration.VERSION_2_3_30)

    init {
        templateConfig.setClassForTemplateLoading(this.javaClass, "/templates")
        templateConfig.defaultEncoding = "UTF-8"
        templateConfig.logTemplateExceptions = false
        templateConfig.whitespaceStripping = true
    }

    fun indexFileName(): String = "index.html"

    fun fileNameFor(areaId: String): String = "$areaId.html"

    fun renderAreaMap(areaMap: AreaMap, outputFileDir: String, options: RenderOptions = RenderOptions()) {
        val template = templateConfig.getTemplate("single-area-map.ftl")
        val fileWriter = FileWriter(Paths.get(outputFileDir, fileNameFor(areaMap.id)).toFile())
        val model = mapOf(
                "areaMap" to areaMap,
                "options" to options,
                "indexLink" to indexFileName(),
                "buildInfo" to buildInfo()
        )
        template.process(model, fileWriter)
    }

    fun renderIndex(outputFileDir: String, areaMaps: List<AreaMap>) {
        val template = templateConfig.getTemplate("index.ftl")
        val fileWriter = FileWriter(Paths.get(outputFileDir, indexFileName()).toFile())

        val indexItems = areaMaps.map {
            MapIndexItem(
                    name = it.name(),
                    author = it.author(),
                    levelDescription = it.levelDescription() ?: "Unknown level",
                    link = fileNameFor(it.id)
            )
        }.sortedWith(compareBy { it.name.lowercase().removePrefix("the ") })

        val model = mapOf(
                "items" to indexItems,
                "buildInfo" to buildInfo()
        )
        template.process(model, fileWriter)
    }

    fun buildInfo() = "Built with MapMaker v${MapMaker.VERSION} at ${ZonedDateTime.now()}"
}
