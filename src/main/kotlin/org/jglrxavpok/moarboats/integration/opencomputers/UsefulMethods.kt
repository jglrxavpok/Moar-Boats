package org.jglrxavpok.moarboats.integration.opencomputers

typealias OCResult = Array<Any?>

fun result(vararg values: Any?): OCResult {
    return arrayOf(*values)
}