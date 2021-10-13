package com.ranieri.tests;

import com.revature.assessors.RevAssess;
import com.revature.assessors.RevaConfig;
import com.revature.assessors.RevaTest;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RevAssess.class)
@RevaConfig(email = "adam.ranieri@revature.com", exerciseId = 202, server = "http://localhost:8080/upload", path = "C:\\Users\\AdamRanieri\\Desktop\\p2")
public class AwesomeTest {

    @RevaTest(points = 1000)
    public void test101(){

    }
}
