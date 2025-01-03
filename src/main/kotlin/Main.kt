class Foo(private val foo: MutableList<Int>) {
    fun f() {
        println(foo)
    }
}
fun main() {
    val a = mutableListOf<Int>()
    val f = Foo(a)

    f.f()

    a.add(1)

    f.f()
}