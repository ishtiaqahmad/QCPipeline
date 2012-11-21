import nl.nmc.*
import nl.nmc.importers.GeneralConfigImporter
import nl.nmc.generalConfig.*

class BootStrap {
    public static def generalConfig;

    def init = { servletContext ->
        new Project(name: "NMC-000-01", description: "some description").save(flush: true)
        new Project(name: "NMC-09-013DEMO", description: "Demo Project").save(flush: true)
        this.generalConfig = importGeneralConfig("general_config.xlsx")   // Filename need to go into grails config or in properties file
    }
    def destroy = {
    }

    private def importGeneralConfig(configFile) {

        def importer = new GeneralConfigImporter(configFile)
        def generalConfigInstance = new GeneralConfig()

        def platformMapList = importer.getPlatformList()
        println "platformMapList = ${platformMapList}"

        platformMapList.each { Map platformParams ->
            def platform = new Platform(platformParams)
            generalConfigInstance.addToPlatforms(platform)
        }
        generalConfigInstance.errors
        def matrixMapList = importer.matrixList
        println "matrixMapList = ${matrixMapList}"

        matrixMapList.each { Map matrixParams ->
            def matrix = new Matrix(matrixParams)
            generalConfigInstance.addToMatrixes(matrix)
        }
        generalConfigInstance.errors
        def additiveMapList = importer.additiveList
        additiveMapList.each { Map additiveParams ->
            def additive = new AdditiveStabilizer(additiveParams)
            generalConfigInstance.addToAdditiveStabilizers(additive)
        }
        generalConfigInstance.errors
        //def generalConfigInstance = new GeneralConfig(platforms:[platformMapList.toList()], matrixes:matrixMapList, additivestabilizers:additiveMapList)
        //[generalConfigInstance:new GeneralConfig(platforms:platformMapList, matrixes:matrixMapList, additivestabilizers:additiveMapList)]

        if (!generalConfigInstance.save(flush: true)) {
            println "Error in loading Config"
        }
        return generalConfigInstance
    }
}
