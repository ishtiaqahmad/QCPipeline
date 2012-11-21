package nl.nmc.generalConfig



import org.junit.*
import grails.test.mixin.*

@TestFor(GeneralConfigController)
@Mock(GeneralConfig)
class GeneralConfigControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/generalConfig/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.generalConfigInstanceList.size() == 0
        assert model.generalConfigInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.generalConfigInstance != null
    }

    void testSave() {
        controller.save()

        assert model.generalConfigInstance != null
        assert view == '/generalConfig/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/generalConfig/show/1'
        assert controller.flash.message != null
        assert GeneralConfig.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/generalConfig/list'

        populateValidParams(params)
        def generalConfig = new GeneralConfig(params)

        assert generalConfig.save() != null

        params.id = generalConfig.id

        def model = controller.show()

        assert model.generalConfigInstance == generalConfig
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/generalConfig/list'

        populateValidParams(params)
        def generalConfig = new GeneralConfig(params)

        assert generalConfig.save() != null

        params.id = generalConfig.id

        def model = controller.edit()

        assert model.generalConfigInstance == generalConfig
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/generalConfig/list'

        response.reset()

        populateValidParams(params)
        def generalConfig = new GeneralConfig(params)

        assert generalConfig.save() != null

        // test invalid parameters in update
        params.id = generalConfig.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/generalConfig/edit"
        assert model.generalConfigInstance != null

        generalConfig.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/generalConfig/show/$generalConfig.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        generalConfig.clearErrors()

        populateValidParams(params)
        params.id = generalConfig.id
        params.version = -1
        controller.update()

        assert view == "/generalConfig/edit"
        assert model.generalConfigInstance != null
        assert model.generalConfigInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/generalConfig/list'

        response.reset()

        populateValidParams(params)
        def generalConfig = new GeneralConfig(params)

        assert generalConfig.save() != null
        assert GeneralConfig.count() == 1

        params.id = generalConfig.id

        controller.delete()

        assert GeneralConfig.count() == 0
        assert GeneralConfig.get(generalConfig.id) == null
        assert response.redirectedUrl == '/generalConfig/list'
    }
}
