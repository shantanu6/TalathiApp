package com.example.talathiapp;

public  class ApiKeys {
   static String  rzpKey,rzpSecret,fbKey,pytmMid,pytmMkey;

    public static String getRzpKey() {
        return rzpKey;
    }

    public static void setRzpKey(String rzpKey) {
        ApiKeys.rzpKey = rzpKey;
    }

    public static String getRzpSecret() {
        return rzpSecret;
    }

    public static void setRzpSecret(String rzpSecret) {
        ApiKeys.rzpSecret = rzpSecret;
    }

    public static String getFbKey() {
        return fbKey;
    }

    public static void setFbKey(String fbKey) {
        ApiKeys.fbKey = fbKey;
    }

    public static String getPytmMid() {
        return pytmMid;
    }

    public static void setPytmMid(String pytmMid) {
        ApiKeys.pytmMid = pytmMid;
    }

    public static String getPytmMkey() {
        return pytmMkey;
    }

    public static void setPytmMkey(String pytmMkey) {
        ApiKeys.pytmMkey = pytmMkey;
    }
}
