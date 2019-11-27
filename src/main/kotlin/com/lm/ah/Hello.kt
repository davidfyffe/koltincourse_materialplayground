package com.lm.ah

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun main(args: Array<String>) {
    Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
    transaction {
        //SchemaUtils.create(Car) // This creates the Car table
        //cars.forEach { it.insert() }  // Inserts from a list of cars we have from http4k
        val cars = Car.selectAllCars() // Selects all cars from the DB
        println(cars) // prints them
    }
}

data class Car(val make: String, val model: String, val id: String = UUID.randomUUID().toString()){
    companion object: Table() {
        val ID = varchar("id", length = 36).primaryKey()
        val MAKE = varchar("make", length = 50)
        val MODEL = varchar("model", length = 50)

        // Lets make a selectAll function to pull out the car class
        fun selectAllCars(): List<Car> = Car.selectAll().map { Car(it[MAKE], it[Car.MODEL], it[Car.ID]) }
    }
    // And an insert function
    fun insert() = Car.insert {
        it[MAKE] = make
        it[MODEL] = model
        it[ID] = id
    }
}