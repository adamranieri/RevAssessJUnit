package com.revature.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.revature.assessors.RevAssess;
import com.revature.assessors.RevaTest;

@ExtendWith(RevAssess.class)
class BasicTests {

	@RevaTest(points = 1000)
	void test() {
		System.out.println("pass");
	}

}
