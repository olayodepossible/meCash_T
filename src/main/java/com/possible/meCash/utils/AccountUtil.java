package com.possible.mecash.utils;

import java.time.Year;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class AccountUtil {

    private AccountUtil(){}
    private static  final Set<Long> generatedNum = new HashSet<>();
    private static final int DIGITS = 10;
    private static final int MIN = (int) Math.pow(10, DIGITS - 1);
    private static final int MAX = (int) Math.pow(10, DIGITS) - 1;
    private static final Random random = new Random();


    public static String generateAccountNumber(){
        long acctNum;
        do {
            acctNum = MIN + (random.nextLong() * (MAX - MIN + 1));
        }while (!generatedNum.add(acctNum));

        return acctNum+"";
    }

}
