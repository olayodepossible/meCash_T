package com.possible.mecash.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Year;
import java.util.*;

public class AccountUtil {

    private AccountUtil(){}
    private static  final Set<String> generatedNum = new HashSet<>();
    private static final int DIGITS = 10;
    private static final int MIN = (int) Math.pow(10, DIGITS - 1);
    private static final int MAX = (int) Math.pow(10, DIGITS) - 1;
    private static final Random random = new Random();


    public static String generateAccountNumber(){
        String acctNum;

        do {
            acctNum = String.valueOf(Math.abs(MIN + (random.nextLong() * (MAX - MIN + 1))));
            if (acctNum.length() > 10){
                acctNum = acctNum.substring(0, 10);
            }
        }while (!generatedNum.add(acctNum));

        return acctNum;
    }

    public static UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }
        return null;
    }

    public static String generateTransactionRef(){

        Year currentYear = Year.now();
        float min = 100000f;
        int max = 999999;

        //generate a random number between min and max
        int randNumber = (int) Math.abs(random.nextInt() * ((currentYear.getValue()+max) - min + 1) + min);
        return String.valueOf(randNumber)+System.currentTimeMillis();
    }

    /*public static double conversion(double amt, String curr, String currConvert, Map<String, Double> currencyList) {

        String conversionKey = curr + "_" + currConvert;
        // Handle direct conversion
        if (currencyList.containsKey(conversionKey)) {
            return amt * currencyList.get(conversionKey);
        }

        // Handle indirect conversion (e.g., NAIRA_POUNDS)
        String[] splitCurr = currConvert.split("_");
        String intermediateCurrency = splitCurr[1];

        // Convert to intermediate currency
        String intermediateConversion = curr + "_" + intermediateCurrency;
        if (currencyList.containsKey(intermediateConversion)) {
            double intermediateRate = currencyList.get(intermediateConversion);
            double intermediateAmount = amt * intermediateRate;

            // Convert intermediate currency to target currency
            String targetConversion = intermediateCurrency + "_" + splitCurr[0];
            if (currencyList.containsKey(targetConversion)) {
                double targetRate = currencyList.get(targetConversion);
                return intermediateAmount * targetRate;
            }
        }

        // No conversion path found
        return 0;
    }*/


    public static double convertCurrency(double amount, String sourceCurrency, String targetCurrency, Map<String, Double> exchangeRates) {
        // Check for direct conversion
        String directConversionKey = sourceCurrency + "_" + targetCurrency;
        if (exchangeRates.containsKey(directConversionKey)) {
            return amount * exchangeRates.get(directConversionKey);
        }

        // Find an intermediate currency
        String intermediateCurrency = findIntermediateCurrency(sourceCurrency, targetCurrency, exchangeRates);
        if (intermediateCurrency == null) {
            return 0;
        }

        double intermediateAmount = convertCurrency(amount, sourceCurrency, intermediateCurrency, exchangeRates);

        return convertCurrency(intermediateAmount, intermediateCurrency, targetCurrency, exchangeRates);
    }

    private static String findIntermediateCurrency(String sourceCurrency, String targetCurrency, Map<String, Double> exchangeRates) {
        for (String key : exchangeRates.keySet()) {
            String[] currencies = key.split("_");
            if (currencies[0].equals(sourceCurrency) || currencies[1].equals(targetCurrency)) {
                return currencies[0].equals(sourceCurrency) ? currencies[1] : currencies[0];
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(generateAccountNumber());
    }

   /* public static double conversion3(double amt, String curr, String currConvert, Map<String, Double> currencyList){
        double ans = 0;
        double temAns = 0;
        // check if the currConvert present in currencyList
        // if true,
        // split the key to check
        // if it match amount currency

        // then fetch the data and multiply by amt
        // if not match, then check the DS to finf the match and do the conversion,
        // then search the match amount currency and finally do the conversion
        // if false
        if(currConvert.contains(curr)){
            if (currencyList.containsKey(currConvert)){
                double rate = currencyList.get(currConvert);
                ans = amt * rate;
            }
        }
        else if(currencyList.containsKey(currConvert)){
            // NAIRA
            // POUNDS_DOLLAR
            // first convert NAIRA_DOLLAR
            // then DOLLAR to POUNDS

            String[] splitCurr = currConvert.split("_");
            String temCur = "";
            if (!splitCurr[0].equalsIgnoreCase(curr)){
                temCur = splitCurr[1];
                String conCur = curr+"_"+temCur;
                if(currencyList.containsKey(conCur)){
                    double rate1 = currencyList.get(conCur);
                    temAns = amt * rate1;

                    if(currConvert.split("_")[1].equalsIgnoreCase(temCur)){
                        double rate = currencyList.get(currConvert);
                        ans = rate * temAns;
                    }
                }
            }

        }
        return  ans;
    }*/

}
