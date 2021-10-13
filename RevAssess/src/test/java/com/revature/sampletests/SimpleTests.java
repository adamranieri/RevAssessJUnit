package com.revature.sampletests;

import com.revature.assessors.RevAssess;
import com.revature.assessors.RevaConfig;
import com.revature.assessors.RevaTest;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RevAssess.class)
@RevaConfig(exerciseId = 101, email = "billy.smith@revature.net", server = "http://localhost:8080/upload", path = "C:\\Users\\AdamRanieri\\Desktop\\tmp")
public class SimpleTests {

    @RevaTest(points = 10)
    public void test1(){

    }
    @RevaTest(points = 50)
    public void test2(){

    }

    @RevaTest(points = 90)
    public void test3(){
        throw new RuntimeException();
    }

}
