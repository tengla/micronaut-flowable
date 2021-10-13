package foo.micronaut.domain

class Train(private val id: Int? = 0, val name: String) {
    override fun toString(): String {
        return "Train<id=$id,name='$name'>"
    }
    fun toJSON(): String {
        return """{"id":$id,"name":"$name"}"""
    }
}
