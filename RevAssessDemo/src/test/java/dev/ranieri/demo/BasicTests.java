package dev.ranieri.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;

import dev.ranieri.assesors.RevAssess;
import dev.ranieri.assesors.RevaTest;


@ExtendWith(RevAssess.class)
public class BasicTests {
	

	@RevaTest(points = 50, tier = 1)
	void test1() {
		System.out.println("Hello");		
	}

	@RevaTest(points = 100, tier = 2)
	void test2() {
		System.out.println("Hola");	
		Assertions.assertEquals(15, 16);
	}
	
	@RevaTest
	void test3() {
		System.out.println("bonjur");	
	}
	
	@RevaTest(points = 200)
	void test4() {
		System.out.println("bonjur");	
	}
	
	@RevaTest(points = 500, tier = 3)
	void test5() {
		System.out.println("bonjur");	
	}
	
}
