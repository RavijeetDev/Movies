package com.ravijeet.movies.util

object FileReaderUtils {

        /**
         * Reads a file with the given name from src/test/resources folder
         * and returns string value
         */
        fun readTestResourceFile(fileName: String): String {
                val fileInputStream = javaClass.classLoader?.getResourceAsStream(fileName)
                return fileInputStream?.bufferedReader()?.readText() ?: ""
        }
}