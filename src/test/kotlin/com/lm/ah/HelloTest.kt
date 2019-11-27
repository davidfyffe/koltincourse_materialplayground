package com.lm.ah

import org.junit.Test

typealias Funk<A, B> = (A) -> B

class HelloTest {


    @Test
    fun curry() {

        // The following function
        val companyAndName: (String, String) -> String = { company, name -> "$company + $name" }
        // Currying this makes the following
        val companyAndNamePartial: (String) -> (String) -> String = { company -> { name -> "$company + $name" } }

        println(companyAndName("LIT", "David"))
        println(companyAndName("LIT", "Paul"))
        println(companyAndName("LIT", "Willie"))


        val partiallyApplied = companyAndNamePartial("LIT")
        println(partiallyApplied("David"))
        println(partiallyApplied("Paul"))
        println(partiallyApplied("Willie"))

    }

    interface i {
        fun print(s : String): String { println("Called $s"); return "Called $s" }
    }

    @Test
    fun listvsSrquenceLazy() {


        class aClass(val inString : String) {
            fun gimmeBack() : String { println("Called $inString"); return "Called $inString" }
        }

        val listOf = listOf(aClass("a"), aClass("b"), aClass("c"), aClass("d"))

//        println(listOf)
//
        val map = listOf.map { it.gimmeBack() }.filter { it == "Called d" }
//
//        println("as map $map")
//
//
        val seq = listOf.asSequence().map { it.gimmeBack() }.filter { it == "Called d" }
//
//        println("as seq $seq")

    }



    @Test
    fun composition() {
        data class Price(val value: Double, val currency: String = "$")
        data class Book(
                val name: String,
                val price: Price,
                val author: String
        )

        val androidBook = Book(
                "Android 6: guida per lo sviluppatore (Italian Edition)",
                Price(39.26, "£"),
                "Massimo Carli"
        )


        // two functions getPrice and formatPrice. The output of getPrice, is the input of formatPrice.
        val getPrice: Funk<Book, Price> = { book -> book.price }
        val formatPrice: Funk<Price, String> = fun(priceData: Price) = "value: ${priceData.value}${priceData.currency}"

        infix fun <A, B, C> Funk<B, C>.after(f: Funk<A, B>): Funk<A, C> = { x: A -> this(f(x)) }
        // 1
        val result: String = formatPrice(getPrice(androidBook))
        println(result)

        // 2
        //A is Book, B is Price, C is String
        val compositeResult: String = (formatPrice after getPrice) (androidBook)
        println(compositeResult)

        //question. What is the type of (formatPrice after getPrice)

    }
}


fun main() {

    // this typealias represents every possible function from A to B
    data class Price(val value: Double, val currency: String = "${'$'}")

    data class Book(
            val name: String,
            val price: Price,
            val author: String
    )

    val androidBook = Book(
            "Android 6: guida per lo sviluppatore (Italian Edition)",
            Price(39.26, "£"),
            "Massimo Carli"
    )

    // two functions getPrice and formatPrice. The output of getPrice, is the input of formatPrice.
    val getPrice: Funk<Book, Price> = { book -> book.price }
    val formatPrice: Funk<Price, String> = fun(priceData: Price) = "value: ${'$'}{priceData.value}${'$'}{priceData.currency}"

    infix fun <A, B, C> Funk<B, C>.after(f: Funk<A, B>): Funk<A, C> = { x: A -> this(f(x)) }
    // 1
    val result: String = formatPrice(getPrice(androidBook))
    println(result)

    // 2
    //A is Book, B is Price, C is String
    val compositeResult: String = (formatPrice after getPrice)(androidBook)
    println(compositeResult)

    //question. What is the type of

}
