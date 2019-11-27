package com.lm.ah;

import org.junit.Test;

import javax.xml.soap.SOAPPart;

public class JavaTest {

    String name = "David";

    @Test
    public void testKotlin() {

        A a = new A("David");

        IAmAObject.INSTANCE.runMe();

        IAmADataClass iAmADataClass = new IAmADataClass("dvaid");
        System.out.println(iAmADataClass);

    }
}
