package com.example.myinstaclone;

public class StringUtils {
    public static String[] splitEmail(String email){
        String[] For_split_email = email.split("[@._]");
        for (int j = 0; j <= For_split_email.length - 1; j++)
        {
            System.out.println("splited emails----------" + For_split_email[j]);
        }
        return For_split_email;
    }
}
