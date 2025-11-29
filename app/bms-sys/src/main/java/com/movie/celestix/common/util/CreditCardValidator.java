package com.movie.celestix.common.util;

public class CreditCardValidator {

    public static boolean isValid(String number) {
        int sum1 = 0, sum2 = 0;
        int count = 0;

        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = number.charAt(i) - '0';
            count++;

            if (count % 2 == 0) {
                int product = digit * 2;
                sum1 += product / 10 + product % 10;
            } else {
                sum2 += digit;
            }
        }

        int total = sum1 + sum2;
        return total % 10 == 0;
    }

    public static String getCardType(String number) {
        int length = number.length();
        int firstDigit = number.charAt(0) - '0';
        int firstTwo = Integer.parseInt(number.substring(0, Math.min(2, length)));

        if (length == 15 && (firstTwo == 34 || firstTwo == 37)) {
            return "AMEX";
        } else if (length == 16 && (firstTwo >= 51 && firstTwo <= 55)) {
            return "MASTERCARD";
        } else if ((length == 13 || length == 16) && firstDigit == 4) {
            return "VISA";
        } else {
            return "UNKNOWN";
        }
    }
}
