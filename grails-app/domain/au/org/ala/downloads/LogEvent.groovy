package au.org.ala.downloads

/**
 * Domain class representation of ala-logger's {@link org.ala.client.model.LogEventVO LogEventVO}
 *
 * @see <a href="https://code.google.com/p/ala-bie/source/browse/trunk/ala-logger/src/main/java/org/ala/client/model/LogEventVO.java">LogEventVO.java</a>
 */
class LogEvent {
    String userEmail
    String comment
    String userIP
    String sourceUrl
    String month
    Integer eventTypeId = 1002
    Integer reasonTypeId
    Integer sourceTypeId = 0
    Map<String, Integer> recordCounts = new Hashtable<String, Integer>()

    Date dateCreated
    Date lastUpdated

    static constraints = {
        comment nullable: true
        month nullable: true
    }
}
