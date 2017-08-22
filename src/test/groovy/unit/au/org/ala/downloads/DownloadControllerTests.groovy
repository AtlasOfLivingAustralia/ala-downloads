package au.org.ala.downloads



import org.junit.*
import grails.test.mixin.*

@TestFor(DownloadController)
@Mock(Download)
class DownloadControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/download/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.downloadInstanceList.size() == 0
        assert model.downloadInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.downloadInstance != null
    }

    void testSave() {
        controller.save()

        assert model.downloadInstance != null
        assert view == '/download/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/download/show/1'
        assert controller.flash.message != null
        assert Download.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/download/list'

        populateValidParams(params)
        def download = new Download(params)

        assert download.save() != null

        params.id = download.id

        def model = controller.show()

        assert model.downloadInstance == download
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/download/list'

        populateValidParams(params)
        def download = new Download(params)

        assert download.save() != null

        params.id = download.id

        def model = controller.edit()

        assert model.downloadInstance == download
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/download/list'

        response.reset()

        populateValidParams(params)
        def download = new Download(params)

        assert download.save() != null

        // test invalid parameters in update
        params.id = download.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/download/edit"
        assert model.downloadInstance != null

        download.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/download/show/$download.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        download.clearErrors()

        populateValidParams(params)
        params.id = download.id
        params.version = -1
        controller.update()

        assert view == "/download/edit"
        assert model.downloadInstance != null
        assert model.downloadInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/download/list'

        response.reset()

        populateValidParams(params)
        def download = new Download(params)

        assert download.save() != null
        assert Download.count() == 1

        params.id = download.id

        controller.delete()

        assert Download.count() == 0
        assert Download.get(download.id) == null
        assert response.redirectedUrl == '/download/list'
    }
}
