package com.academic.examapp.utils;

public class KMPAlgorithm {
    public static int KMPSearch(String pattern, String text) {
        int[] lps = computeLPSArray(pattern);
        int i = 0, j = 0, count = 0;
        while (i < text.length()) {
            if (pattern.charAt(j) == text.charAt(i)) {
                j++; i++;
            }
            if (j == pattern.length()) {
                count++;
                j = lps[j - 1];
            } else if (i < text.length() && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) j = lps[j - 1];
                else i++;
            }
        }
        return count;
    }

    private static int[] computeLPSArray(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0, i = 1;
        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) len = lps[len - 1];
                else lps[i++] = 0;
            }
        }
        return lps;
    }
}
