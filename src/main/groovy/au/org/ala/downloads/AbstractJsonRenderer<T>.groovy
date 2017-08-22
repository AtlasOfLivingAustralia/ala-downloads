package au.org.ala.downloads

import com.google.gson.Gson
import com.google.gson.stream.JsonWriter
import grails.rest.Link
import grails.rest.render.util.AbstractLinkingRenderer
import groovy.transform.CompileStatic
import org.grails.web.databinding.bindingsource.DataBindingSourceRegistry
import org.grails.web.databinding.bindingsource.HalJsonDataBindingSourceCreator
import grails.web.mapping.LinkGenerator
import grails.web.mime.MimeType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.converter.Converter

import javax.annotation.PostConstruct
import javax.xml.bind.DatatypeConverter

/**
 * Created by bea18c on 15/04/2014.
 */
@CompileStatic
abstract class AbstractJsonRenderer<T> extends AbstractLinkingRenderer<T> {

    public static final String LINKS_ATTRIBUTE = "_links"
    public static final String EMBEDDED_ATTRIBUTE = "_embedded"

    LinkGenerator grailsLinkGenerator

    @Autowired(required = false)
    void setGrailsLinkGenerator(LinkGenerator grailsLinkGenerator) {
        this.grailsLinkGenerator = grailsLinkGenerator
    }

    private static class UTCDateConverter implements Converter<Date, String> {
        private final static TimeZone UtcTZ = TimeZone.getTimeZone('UTC')
        @Override
        String convert(Date source) {
            final GregorianCalendar cal = new GregorianCalendar()
            cal.setTime(source)
            cal.setTimeZone(UtcTZ)
            DatatypeConverter.printDateTime(cal)
        }
    }

    private Gson gson = new Gson()

    @Autowired(required = false)
    void setGson(Gson gson) {
        this.gson = gson
    }

    Converter<Date, String> dateToStringConverter = new UTCDateConverter()

    @Autowired(required = false)
    void setDateToStringConverter(Converter<Date, String> converter) {
        this.dateToStringConverter = converter
    }

    @Autowired(required = false)
    DataBindingSourceRegistry dataBindingSourceRegistry

    @PostConstruct
    void initialize() {
        if (dataBindingSourceRegistry != null) {
            final thisType = getTargetType()
            final thisMimeTypes = getMimeTypes()
            final halDataBindingSourceCreator = new HalJsonDataBindingSourceCreator() {
                @Override
                Class getTargetType() {
                    thisType
                }

                @Override
                MimeType[] getMimeTypes() {
                    thisMimeTypes
                }
            }
            halDataBindingSourceCreator.properties.gson = gson
            dataBindingSourceRegistry.addDataBindingSourceCreator(halDataBindingSourceCreator)
        }
    }

    AbstractJsonRenderer(Class<T> targetType, MimeType mimeType) {
        super(targetType, mimeType)
    }

    AbstractJsonRenderer(Class<T> targetType, MimeType[] mimeTypes) {
        super(targetType, mimeTypes)
    }

    protected void writeLink(Link link, Locale locale, writerObject) {
        JsonWriter writer = (JsonWriter)writerObject
        writer.name(link.rel)
        writeInternalLink(link, locale, writer)
    }

    protected void writeArrayLink(Link link, Locale locale, JsonWriter writerObject) {
        writeInternalLink(link, locale, (JsonWriter)writerObject)
    }

    private void writeInternalLink(Link link, Locale locale, JsonWriter writer) {
        writer.beginObject()
                .name(HREF_ATTRIBUTE).value(link.href)
                .name(HREFLANG_ATTRIBUTE).value((link.hreflang ?: locale).language)
        final title = link.title
        if (title) {
            writer.name(TITLE_ATTRIBUTE).value(title)
        }
        final type = link.contentType
        if (type) {
            writer.name(TYPE_ATTRIBUTE).value(type)
        }
        if (link.templated) {
            writer.name(TEMPLATED_ATTRIBUTE).value(true)
        }
        if (link.deprecated) {
            writer.name(DEPRECATED_ATTRIBUTE).value(true)
        }
        writer.endObject()
    }

    protected void writeDomainProperty(value, String propertyName, writer) {
        final jsonWriter = (JsonWriter) writer
        if(value instanceof Number) {
            jsonWriter.name(propertyName).value((Number)value)
        }
        else if(value instanceof CharSequence || value instanceof Enum) {
            jsonWriter.name(propertyName).value(value.toString())
        }
        else if(value instanceof Date) {
            final asStringDate = dateToStringConverter.convert((Date)value)
            jsonWriter.name(propertyName).value(asStringDate)
        }
        else {
            jsonWriter.name(propertyName)
            gson.toJson(value, value.class, jsonWriter)
        }
    }

}
