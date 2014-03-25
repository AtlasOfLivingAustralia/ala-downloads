package au.org.ala.downloads

import groovy.xml.MarkupBuilder

import java.text.DecimalFormat

class DownloadsTagLib {

    static namespace = 'dl'

    def navSeperator = { attrs, body ->
        out << "&nbsp;&#187;&nbsp;"
    }

    /**
     * @attr active
     * @attr title
     * @attr href
     */
    def breadcrumbItem = { attrs, body ->
        def active = attrs.active
        if (!active) {
            active = attrs.title
        }
        def current = pageProperty(name:'page.pageTitle')?.toString()

        def mb = new MarkupBuilder(out)
        mb.li(class: active == current ? 'active' : '') {
            a(href:attrs.href) {
                i(class:'icon-chevron-right') { mkp.yieldUnescaped('&nbsp;')}
                mkp.yield(attrs.title)
            }
        }
    }

    def sizeInBytes = { attrs, body ->
        def size = attrs.size as Long
        if (size) {
            def labels = [' bytes', 'KB', 'MB', 'GB']
            def label = labels.find { (size < 1024) ? true : { size /= 1024; false }() } ?: 'TB'
            out << "${new DecimalFormat('0.#').format(size)} $label"
        } else {
            out << '??'
        }
    }


}
