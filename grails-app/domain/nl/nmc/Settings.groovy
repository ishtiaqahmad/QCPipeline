package nl.nmc

class Settings {
    Date dateCreated
    Date lastUpdated
    HashMap settings

    static constraints = {

    }

    static belongsTo = [project: Project]
}
