package nl.nmc

import nl.nmc.exporters.JobListExporter
import nl.nmc.general.config.AdditiveStabilizer
import nl.nmc.general.config.GeneralConfig
import nl.nmc.general.config.Matrix
import nl.nmc.general.config.Platform

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
        [project: Project.get(params.id), config: GeneralConfig.get(1)]
    }

    def addSetting() {
        def project = Project.get(params.id) ?: null
        if (params?.submit == "createProjectSetting" && project) {
            def platform = Platform.get(params.platform) ?: null
            def matrix = Matrix.get(params.matrix) ?: null
            def additive = AdditiveStabilizer.get(params.additive) ?: null
            def opt = params.options as List ?: null

            if (platform && matrix && additive) {
                def setting = new Settings(name: params.name, settings: ['options': opt], project: project, platforms: platform, matrixes: matrix, additiveStabilizers: additive)
                if (!setting.save(flush: true)) {
                    flash.message = "was unable to save the Setting - ${params}"
                }
            }

        }

        //return to where you came from...
        redirect(action: "view", params: [id: params.id])
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

    def prepareQCReport() {
        if (!params?.id) {
            redirect(action: "index", params: params)
        }

        def project = Project.get(params.id) ?: null
        def setting = Settings.get(params.setting) ?: null
        def dataList = project?.datas

        if (params?.submit == "prepare QC Report" && project && setting && dataList) {
            //cross validate it
            if (setting.project == project) {

                def folderLocation = "/Users/ishtiaq/VMShare/Data/DataJobs_v3/"
                def jobListLocationFile = new File("${folderLocation}")
                jobListLocationFile.mkdirs()

                def jobListFile = new File("${jobListLocationFile}/DCL Joblist_v3.xlsx")
                JobListExporter jobListExporter

                if (jobListFile.exists())
                    jobListExporter = new JobListExporter(jobListFile.absolutePath)
                else
                    jobListExporter = new JobListExporter()
                jobListExporter.addSetting(setting)
                jobListExporter.export()

                jobListFile.setWritable(true, false) && jobListFile.canWrite()
                FileOutputStream fileOut = new FileOutputStream(jobListFile);
                jobListExporter.save(fileOut)
                fileOut.close();
                println(jobListFile)

                // add measurements files to folder stature
                def projectFolderLocation = "${folderLocation + setting.platforms.toArray()[0].toString()}/${project.name}"
                println projectFolderLocation
                def projectFolder = new File("${projectFolderLocation}")
                projectFolder.mkdirs()
                def inputFolder = new File("${projectFolderLocation}/input")
                inputFolder.mkdir()
                def meaFolder = new File("${projectFolderLocation}/mea")
                meaFolder.mkdir()
                def outputFolder = new File("${projectFolderLocation}/output")
                outputFolder.mkdir()

                def ant = new AntBuilder()
                dataList.each {
                    ant.copy(file: new File(project.nameOfDirectory() + "/" + it.filename), tofile: new File(projectFolderLocation + "/mea/" + it.name), overwrite: true)
                }

                //zippedFile.setWritable(true, false) && zippedFile.canWrite()
                ant.zip(
                        basedir: projectFolderLocation,
                        destfile: "${folderLocation}/${project.name}.zip",
                        level: 9
                )
                def zippedFile = new File("${folderLocation}/${project.name}.zip")
                if (zippedFile.exists()) {
                    response.setContentType("application/octet-stream")
                    response.setHeader("Content-disposition", "filename=${project.name}.zip")
                    response.outputStream << zippedFile.bytes
                    return
                }

                /*
                ProcessBuilder processBuilder = new ProcessBuilder("/Applications/Microsoft Office 2011/Microsoft Excel.app/Contents/MacOS/Microsoft Excel")
                processBuilder = processBuilder.directory(jobListLocationFile)
                processBuilder = processBuilder.command("${file}")
                Process p = processBuilder.start()
                p.waitFor()

                def result = [project: project, setting: setting]

                render result as JSON
                */
            } else
                println "Error: Project and Setting doesnot crospond to each other"
        }
        redirect(action: "index", params: [])
    }
}
