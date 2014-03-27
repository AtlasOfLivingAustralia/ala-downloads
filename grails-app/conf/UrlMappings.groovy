class UrlMappings {

	static mappings = {

        "/projects/$name" {
            controller = 'project'
            action = 'findByName'
        }
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(controller: "home")
		"500"(view:'/error')
	}
}
