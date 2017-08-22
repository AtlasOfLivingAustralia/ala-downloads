package au.org.ala.downloads

class UrlMappings {

	static mappings = {

        name projects: "/admin" {
            controller = 'admin'
            action = 'index'
        }
        name projects: "/p" {
            controller = 'project'
            action = 'list'
        }
        name projectByName: "/p/$projectName" {
            controller = 'project'
            action = 'artifactListByName'
        }
        name artifactDetailsByName: "/p/$projectName/$file/details" {
            controller = 'project'
            action = 'artifactDetailsByName'
        }
        name downloadByFile: "/p/$projectName/$file" {
            controller = 'project'
            action = 'downloadArtifactByName'
        }
        name feedByProjectName: "/feed/p/$projectName" {
            controller = 'feed'
            action = 'artifacts'
        }

        "/ws/projects"(resources:'projectResource', controller: 'projectResource', excludes:['save','edit','update','delete']) {
            "/artifacts"(resources:'projectArtifactResource', controller: 'projectArtifactResource', excludes:['save','edit','update','delete'])
        }

		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
                controller != 'project'
			}
		}

		"/"(controller: "home")
		"500"(view:'/error')
	}
}
