package com.lm.ah

import arrow.aql.extensions.list.select.*
import arrow.aql.extensions.listk.select.select
import arrow.aql.extensions.list.where.*
import arrow.aql.extensions.list.groupBy.*
import arrow.aql.extensions.listk.select.selectAll
import arrow.aql.extensions.id.select.value
import arrow.core.*


import arrow.core.extensions.fx
import org.junit.Test
import java.lang.Exception
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import arrow.core.extensions.either.apply.*
import arrow.core.extensions.id.foldable.get
import arrow.effects.IO
import kotlinx.coroutines.*


//import arrow.core.extensions.fx

class Arrow {

    private fun handleFailure(error: Throwable): Unit = println("An Error Occured: ${error.message}")
    private fun handleSuccess(speakerNames: List<String>): Unit = println("We have some speakers: ${speakerNames.joinToString(separator = ",")}")

    class Speaker(val name: String) {
    }
    class SpeakerService {
        fun loadAllSpeakers(): List<Speaker> {
            return listOf(Speaker("David"), Speaker("Paul"))
        }

    }
    class ApiClient(private val service: SpeakerService) {
        fun getSpeakers(): Try<List<Speaker>> = Try {
            service.loadAllSpeakers()
        }
    }

    @Test
    fun eagerTry() {
        val t = Try { println("Run me")}
    }

    @Test
    fun usingIO() {

        val e = IO { throw Exception("Bad ex") }
        val s = IO { println("Run me") }
    }

    @Test
    fun usingIOToDoSomething()  {

        fun getUserFromNetwork(w: Int): IO<String> {
            return IO {
                if (w == 0) throw Exception("Not this time")
                else "User(David)"
            }
        }

//        val a = IO<Int> { throw Exception("Booomd") }
//                .unsafeRunAsync { result ->
//                    result.fold({ println("Error") }, { println(it.toString()) })
//                }
//
//        runBlocking {
//
//            val b = IO<Int> { throw Exception("Bad Exception") }
//                    .attempt()
//
//            b.attempt().map { it.fold({println("In error $it")}, {println("All ok. $it")}) }
//
//
//        }

//        suspend fun sayHello(): Unit =
//                println("Hello World")
//
//        suspend fun sayGoodBye(): Unit =
//                println("Good bye World!")
//
//        fun greet(): IO<Unit> =
//                IO.fx {
//                    !effect { sayHello() }
//                    !effect { sayGoodBye() }
//                }


//        val b = IO<Int> { 2 }
//                .runAsync { result ->
//                    result.fold({ IO { println("Error") } }, { IO { println(it.toString()) } })
//                }



        //lets attempt the network call.
//        getUserFromNetwork(0).unsafeRunAsync { result ->
//            println("did it work? isLeft ${result.isLeft()}")
//            println("did it work? isRight ${result.isRight()}")
//        }


        runBlocking {
            getUserFromNetwork(1).attempt().map { result ->
                println("did it work? isLeft ${result.isLeft()}")
                println("did it work? isRight ${result.isRight()}")

                result.map { println(it) }
            }
        }

    }

    @Test
    fun usingTry() {

        val service = SpeakerService()
        val apiClient = ApiClient(service)

        apiClient.getSpeakers().map { speakers ->
            speakers.map { it.name }
        }.fold(
                ifFailure = { error -> handleFailure(error) },
                ifSuccess = { names -> handleSuccess(names) }
        )


    }



    @Test
    fun basictrycatch() {
        fun getUserFromNetwork() : String { throw Exception("Not this time")}

        class A {
            lateinit var name: String

            fun getName() {
                try {
                    name = getUserFromNetwork()
                } catch (ex: Exception) {
                    println("Error $ex")
                }
                if (::name.isInitialized) {
                    println("Name is $name")
                } else {
                    println("Something went wrong")
                }
            }
        }

        A().getName()
    }

    @Test
    fun basicTry() {

        fun getUserFromNetwork() = Try { throw Exception("Not this time")}

        getUserFromNetwork().map {
            println("Name is $it")
        }

    }

    @Test
    fun divByZeroSafely() {

        fun divisor(a: Int, b: Int): Either<String, Int> {
            return if(b == 0) "Cannot Divide By 0".left() else (a / b).right()
        }

        val right = divisor(3, 1)
        assertTrue(right.isRight())
        right.map {
            assertEquals(3, it)
        }


        val left = divisor(3, 0)
        assertTrue(left.isLeft())
        left.fold(
                { assertEquals("Cannot Divide By 0", it) },
                { println(it) }
        )

    }

    @Test
    fun basicEither() =  runBlocking {
        suspend fun getUserFromNetwork(w: Int): Either<String, String> {
            return Either.catch {
                if (w == 0) throw Exception("Not this time")
                else "User(David)"
            }.fold(
                    { "Opps. Something went wrong. $it".left() },
                    { it.right() }
            )
        }


        val user = getUserFromNetwork(0)

        println("did it work? isLeft ${user.isLeft()}")
        println("did it work? isRight ${user.isRight()}")

        user.map {
            println("User Object is $it")
        }

        user.fold(
                { println("Folding on left scenario $it") },
                { println("Folding on right scenario $it") }
        )
    }


    @Test
    fun gettingTheValues() {

        fun divisor(a: Double, b: Double): Either<String, Double> {
            return if (b == 0.0) "Cannot Divide By 0".left() else (a / b).right()
        }

        val a = divisor(1.0, 2.0)
        val b = divisor(2.0, 4.0)
        val c = divisor(4.0, 8.0)

        val asApplicative = tupled(a, b, c)


        asApplicative.map {
            println("a = ${it.a}")
            println("b = ${it.b}")
            println("c = ${it.c}")
            println("a+b+c = ${it.a + it.b + it.c}")
        }


        val result = a.fold(
                { throw Exception("bad a") },
                { a ->
                    a + b.fold(
                            { throw Exception("bad b") },
                            { b ->
                                b + c.fold(
                                        { throw Exception("bad c") },
                                        { c -> c }
                                )
                            }
                    )
                }
        )

        println(result)
    }

    @Test
    fun usingBinding() {

        fun divisor(a: Int, b: Int): Either<String, Int> {
            return if(b == 0) "Cannot Divide By 0".left() else (a / b).right()
        }

        val result = Either.fx<String, Int> {

            val a = divisor(1,2).bind()
            val b = divisor(2,4).bind()
            val c = divisor(4,8).bind()

            a + b + c

        }

        result.map { println(it) }

    }

    @Test
    fun selectFromList() {

        val result: List<Int> =
                listOf(1, 2, 3).query {
                    select { this + 1 }
                }.value()

        println(result)

    }

    @Test
    fun selectWhere() {
        data class Person(val name : String, val age : Int)

        val result: List<String> =
                listOf(Person("john", 10),
                        Person("jane", 20),
                        Person("jack", 30)).query {
                    select { name } where { age >= 20 }
                }.value()

        println(result)
    }

    @Test
    fun selectGroupBy() {

        data class Person(val name : String, val age : Int)

        val result  =
                listOf(Person("john", 10),
                        Person("jane", 20),
                        Person("jack", 30),
                        Person("jack", 20)).query {
                    selectAll() where { age > 10 } groupBy { name }
                }.value()

        println(result)
        result.map { println(it["jack"]) }

    }



}


