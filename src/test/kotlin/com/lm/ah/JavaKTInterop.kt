package com.lm.ah

import org.junit.Test

data class IAmADataClass(val name: String)

class A(val name : String)
{
    companion object
    {
        val name = ""
        var age = 0
        fun runMe() = println("runMe")
    }

}


class JavaKTInterop {

  @Test
  fun testJava() {

//      val a = A("David")
//
//      val static = Static()
//      static
//
//      Static.runMe()


      fun JavaTest.newExt(i : Int): String {
          return "${this.name} $i"
      }

      val jt = JavaTest()
      println(jt.newExt(100))

  }

}

object IAmAObject {
    fun runMe() = println("runMe")
}