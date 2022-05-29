package com.example.todolistapp;

public class Aylar {

    private static int ay;
    private static String sAy;

    public Aylar(int ay) {
        Aylar.ay = ay;
    }

    public static void setAy(int ay) {
        Aylar.ay = ay;
    }

    static public String getsAy() {
        switch (ay) {
            case 0:
                Aylar.sAy = "OCAK";
                break;
            case 1:
                Aylar.sAy = "SUBAT";
                break;
            case 2:
                Aylar.sAy = "MART";
                break;
            case 3:
                Aylar.sAy = "NISAN";
                break;
            case 4:
                Aylar.sAy = "MAYIS";
                break;
            case 5:
                Aylar.sAy = "HAZIRAN";
                break;
            case 6:
                Aylar.sAy = "TEMMUZ";
                break;
            case 7:
                Aylar.sAy = "AGUSTOS";
                break;
            case 8:
                Aylar.sAy = "EYLUL";
                break;
            case 9:
                Aylar.sAy = "EKIM";
                break;
            case 10:
                Aylar.sAy = "KASIM";
                break;
            case 11:
                Aylar.sAy = "ARALIK";
                break;

        }
        return sAy;
    }

    static public int getsAyInNumber(String _say) {
        switch (_say) {
            case "OCAK":
            case "Jan":
                Aylar.ay = 0;
                break;
            case "SUBAT":
            case "Feb":
                Aylar.ay = 1;
                break;
            case "MART":
            case "Mar":
                Aylar.ay = 2;
                break;
            case "NISAN":
            case "Apr":
                Aylar.ay = 3;
                break;
            case "MAYIS":
            case "May":
                Aylar.ay = 4;
                break;
            case "HAZIRAN":
            case "Jun":
                Aylar.ay = 5;
                break;
            case "TEMMUZ":
            case "Jul":
                Aylar.ay = 6;
                break;
            case "AGUSTOS":
            case "Aug":
                Aylar.ay = 7;
                break;
            case "EYLUL":
            case "Sep":
                Aylar.ay = 8;
                break;
            case "EKIM":
            case "Oct":
                Aylar.ay = 9;
                break;
            case "KASIM":
            case "Nov":
                Aylar.ay = 10;
                break;
            case "ARALIK":
            case "Dec":
                Aylar.ay = 11;
                break;
        }
        return ay;
    }
}
