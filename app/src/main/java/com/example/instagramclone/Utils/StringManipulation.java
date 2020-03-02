package com.example.instagramclone.Utils;

import java.util.Random;

public class StringManipulation {

    public static String expandUsername(String username) {
        return username.replace("."," ");
    }

    public static String condenseUsername(String username) {
        return username.replace(" ", ".");
    }

    public static String getTags(String string) {
        if (string.contains("#")) {
            StringBuilder sb = new StringBuilder();
            char[] charArray = string.toCharArray();
            Boolean foundWord = false;
            for (char c : charArray) {
                if (c == '#') {
                    foundWord = true;
                    sb.append(c);
                } else {
                    if (foundWord) {
                        sb.append(c);
                    }
                }
                if (c == ' ') {
                    foundWord = false;
                }
            }
            String s = sb.toString().replace(" ","").replace("#",",#");
            return s.substring(1);
        }
        return "";
    }

    /**
     * in-> I like california #california #sea #icecream
     *
     * out<- #california#sea#icecream
     */

    //#baby I like Yannis #cute #teddy#like


    public static String generateRandomChars() {
        String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        int length = 19;
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(s.charAt(random.nextInt(s
                    .length())));
        }

        return sb.toString();
    }

}
