package me.kject.dependency.trace

enum class RequestType(private val text: String) {

    REQUEST("@Request"),

    CONSTRUCTOR("constructor"),

    INITIALIZE("@Initialize function");

    override fun toString() = text

}