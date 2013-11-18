modules = {
    application {
        resource url:'js/application.js'
    }

    bootstrapCombobox {
        dependsOn 'bootstrap'
        resource url:'js/bootstrap-combobox.js'
        resource url:'css/bootstrap-combobox.css'
    }
}