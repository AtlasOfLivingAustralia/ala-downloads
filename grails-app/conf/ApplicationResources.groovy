modules = {
    application {
        resource url:'js/application.js'
    }

    bootstrapCombobox {
        dependsOn 'bootstrap'
        resource url:'js/bootstrap-combobox.js'
        resource url:'css/bootstrap-combobox.css'
    }

    jqueryTmpl {
        dependsOn 'jquery'
        //resource url:'js/jquery.loadTemplate-1.4.3.js'
        resource url:'js/jquery.loadTemplate-1.4.3.min.js', exclude: 'minify'
    }
}