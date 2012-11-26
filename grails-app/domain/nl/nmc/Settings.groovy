package nl.nmc

import nl.nmc.general.config.AdditiveStabilizer
import nl.nmc.general.config.Matrix
import nl.nmc.general.config.Platform

class Settings {
    Date dateCreated
    Date lastUpdated
    HashMap settings
    String name

    static constraints = {

    }

    @Override
    String toString() {
        return name
    }

    static belongsTo = [project: Project]
    static hasMany = [additiveStabilizers: AdditiveStabilizer, matrixes: Matrix, platforms: Platform]
}
