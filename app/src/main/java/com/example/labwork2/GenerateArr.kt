package com.example.labwork2

import kotlin.math.roundToInt

fun parseInputFromFile(src: String) : Array<Array<Int>> {
    val split: List<String> = src.split("\n").filter { i -> i != ""}
    val linesLength: Int = split.size
    val lineBegin: Int?
    lineBegin = if (split.contains("/*"))
        split.indexOf("/*")+1
    else
        split.size
    val array: List<List<Int>> = split.dropLast(linesLength-lineBegin+1).map { s -> s.split(" ").map {
            i -> i.toDouble().toInt() } }
    return array.map { e -> e.toTypedArray()}.toTypedArray()
}

fun generateArr() : Array<Array<Int>> {
    return Array(10) { i -> Array(6000 + i*1000) {
        ((Math.random() - 0.5) * 100).roundToInt()} }
}
