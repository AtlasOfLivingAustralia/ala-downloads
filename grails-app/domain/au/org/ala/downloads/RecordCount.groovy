package au.org.ala.downloads

class RecordCount {

    String code
    Integer records

    static constraints = {
    }

    String toString() {
        "${code}: ${records}"
    }
}
