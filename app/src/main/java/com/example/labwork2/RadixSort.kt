package com.example.labwork2

class RadixSort(array: Array<Int>) {

    val time: Double
    val arr: Array<Int> = array
    private val radix = array.size
    val sorted: Array<Int>

    init {
        val beginTime: Long = System.currentTimeMillis()
        this.sorted = sort()
        val endTime: Long = System.currentTimeMillis()
        this.time = (endTime - beginTime)/100.0
    }

    fun sort(): Array<Int> {
        val sortedArr: Array<Int> = arr

        var minValue: Int = sortedArr[0]
        var maxValue: Int = sortedArr[0]
        for (i in 1 until sortedArr.size) {
            if (sortedArr[i] < minValue) {
                minValue = sortedArr[i]
            } else if (sortedArr[i] > maxValue) {
                maxValue = sortedArr[i]
            }
        }

        var exponent = 1
        while ((maxValue - minValue) / exponent >= 1) {
            countingSortByDigit(sortedArr, radix, exponent, minValue)
            exponent *= radix
        }
        return sortedArr
    }

    private fun countingSortByDigit(array: Array<Int>, radix: Int, exponent: Int, minValue: Int) {
        var bucketIndex: Int
        val buckets = IntArray(radix)
        val output = IntArray(array.size)
        // Initialize bucket
        for (i in 0 until radix) {
            buckets[i] = 0
        }
        // Count frequencies
        for (i in array.indices) {
            bucketIndex = ((array[i] - minValue) / exponent % radix)
            buckets[bucketIndex]++
        }
        // Compute cumulates
        for (i in 1 until radix) {
            buckets[i] += buckets[i - 1]
        }
        // Move records
        for (i in array.indices.reversed()) {
            bucketIndex = ((array[i] - minValue) / exponent % radix)
            output[--buckets[bucketIndex]] = array[i]
        }
        // Copy back
        for (i in array.indices) {
            array[i] = output[i]
        }
    }
}
