grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.7
grails.project.source.level = 1.7
//grails.project.war.file = "target/ROOT##${appName}-${appVersion}-${new Date().format('yyyy_MM_dd-HH_mm')}.war"

grails.project.fork = [
    // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
    //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

    // configure settings for the test-app JVM, uses the daemon by default
    test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
    // configure settings for the run-app JVM (disabled for IntelliJ debugging
    //run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    run: false,
    // configure settings for the run-war JVM
    war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the Console UI JVM
    console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        mavenLocal()
        mavenRepo ("https://nexus.ala.org.au/content/groups/public/") {
            updatePolicy 'always'
        }
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.
        runtime 'org.postgresql:postgresql:42.1.3.jre7'
        compile "au.org.ala:ala-logger:1.3"
        compile 'com.google.guava:guava:19.0'
    }

    plugins {
        build ":release:3.0.1"
        // plugins for the build system only
        build ":tomcat:7.0.70"

        // plugins for the compile step
        compile ":scaffolding:2.1.2"
        compile ":cache:1.1.8"
        // additions
        compile ":quartz:1.0.1"
        compile ":feeds:1.6"
        compile ":qrcode:0.7"
        compile ":markdown:1.1.1"
        compile ":pretty-time:2.1.3.Final-1.0.1"
        compile ":ala-ws-plugin:1.7"
        compile "org.grails.plugins:asset-pipeline:2.14.1"

        // plugins needed at runtime but not for compilation
        runtime ":hibernate4:4.3.10" // or ":hibernate:3.6.10.18"
        runtime ":database-migration:1.3.8"
        runtime ":jquery:1.11.1"

        // additions
        runtime ":ala-bootstrap3:2.0.0"
        runtime ":ala-auth:2.1.3"
    }
}
