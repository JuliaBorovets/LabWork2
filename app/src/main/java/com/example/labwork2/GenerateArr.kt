package com.example.labwork2

import kotlin.math.roundToInt

fun parseInputFromFile(src: String) : Array<Array<Int>> {
    val split: List<String> = src.split("\n").filter { i -> i != ""}
    val lines_length: Int = split.size
    val comment_line_begin: Int?
    if (split.contains("/*"))
        comment_line_begin = split.indexOf("/*")+1
    else
        comment_line_begin = split.size
    val array = split.dropLast(lines_length-comment_line_begin+1).map { s -> s.split(" ").map { i -> i.toDouble().toInt() } }
    return array.map { e -> e.toTypedArray()}.toTypedArray()
}

fun generateArr() : Array<Array<Int>> {
    return Array(10) { i -> Array(100 + i*100) {
        ((Math.random() - 0.5) * 100).roundToInt()} }
}
