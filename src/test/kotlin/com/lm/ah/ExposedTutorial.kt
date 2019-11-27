package com.lm.ah


import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test

class ExposedTutorial {

    object Car: Table("Car"){ // Notice this is a singleton
        val ID = varchar("id", length = 36).primaryKey()
        val MAKE = varchar("make", length = 50)
        val MODEL = varchar("model", length = 50)
    }

    @Test
    fun createConnection() {

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            SchemaUtils.create(Car)

            val carID = Car.insert {
                it[ID] = "1"
                it[MAKE] = "Vauxhall"
                it[MODEL] = "Cavalier"
            }

            Car.selectAll().forEach {
                println("id=${it[Car.ID]} make=${it[Car.MAKE]}, model=${it[Car.MODEL]}")
            }

        }


//        val makes: List<String> = Car.selectAll().map { it[Car.MAKE] }
//
//// Lets pull out something more useful
//        val makesAndModels = Car.selectAll().map { it[Car.MAKE] to it[Car.MODEL] }

        println("")

    }
}