package com.possible.meCash.utils;

import java.time.Year;
import java.util.Random;

public class AccountUtil {

    private AccountUtil(){}
    private static final Random random = new Random();
    public static String generateAccountNumber(){

        Year currentYear = Year.now();
        float min = 100000f;
        int max = 999999;

        //generate a random number between min and max
        int randNumber = (int) Math.floor(random.nextInt() * (max - min + 1) + min);
        //convert the currentYear and randomNumber to strings, then concatenate them

        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randNumber);

        return year + randomNumber;
    }
}
