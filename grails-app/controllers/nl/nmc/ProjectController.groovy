package nl.nmc
import nl.nmc.generalConfig.*

class ProjectController {
    enum AcceptedExt {
        xlsx,
        xls,
        csv,
        tab,
        tsv,
        txt;
    }

    def index() {
        if (params?.submit == "createNewProject") {
            def project = new Project(params)
            if (!project.save(flush: true)) {
                flash.message = "was unable to save the Project - ${params}"
            }
        }
        [projects: Project.list()]
    }

    def view() {
        if (!params?.id) {
            redirect(action: "index", params: params)
        }
        [project: Project.get(params.id), config:GeneralConfig.get(1)]
    }

    def addSetting() {
        def project = Project.get(params.id) ?: null
        if (project) {
            def setting = new Settings(settings: [:])
        }
    }

    def addFile() {
        def uploadedfile = request.getFile('fileUpload')

        def project = Project.get(params.id) ?: null

        if (!uploadedfile.isEmpty() && project) {

            //determine extension of uploaded file
            def filename = uploadedfile.getOriginalFilename().toLowerCase()
            def ext = filename.tokenize(".")[-1]

            // select correct way of processing the file based on extension
            switch (ext) {

                case 'zip':    // uploaded zip
                    def zipLocation = "/tmp/${UUID.randomUUID().toString()}"
                    def zipLocationFile = new File("${zipLocation}")
                    zipLocationFile.mkdirs()

                    def zippedFile = new File("${zipLocation}/zippedFile.zip")
                    zippedFile.setWritable(true, false) && zippedFile.canWrite()

                    uploadedfile.transferTo(zippedFile)

                    def ant = new AntBuilder()
                    ant.unzip(
                            src: zippedFile.canonicalPath,
                            dest: zipLocation,
                            overwrite: "true"
                    )

                    //loop over files from zip and add the mzxml files!!!
                    zipLocationFile.eachFile {

                        //determine extension of unzipped files
                        def unzippedFilename = it.absolutePath.toLowerCase()
                        def unzippedName = unzippedFilename.tokenize("/")[-1]
                        def unzippedExt = unzippedName.tokenize(".")[-1]

                        if (unzippedExt in AcceptedExt) {
                            def unzippedData = new Data(name: "${unzippedName}", project: project, filename: "${UUID.randomUUID().toString()}").save(flush: true)
                            ant.copy(file: it, tofile: new File(project.nameOfDirectory() + "/" + unzippedData.filename), overwrite: true)
                        }
                    }

                    break

                case 'xlsx':
                case 'xls':
                case 'csv':
                case 'tsv':
                case 'tab':
                case 'txt':
                    def newData = new Data(name: "${filename}", project: project, filename: "${UUID.randomUUID().toString()}")
                    newData.save(flush: true)
                    uploadedfile.transferTo(new File(project.nameOfDirectory() + "/" + newData.filename))
                    break

                default:    //unsupported file!
                    log.error("Unsupported filetype!")
            }
        }

        //return to where you came from...
        redirect(action: "view", params: params)
    }

}
