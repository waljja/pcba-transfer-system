package com.ht.constants;

/**
 * 特殊权限判断
 *
 * @author 张越
 */
public class Constants {

    public static final String[] NAME = {
            "蒋晓琴", "谭长蓉", "秦丹", "陆汉云", "黄幸红", "林小龙"
    };
    public static final String[] ACCOUNT = {
            "DD20312064", "EM0011173", "DM20327029", "HM2039945"
    };

    public static boolean isNames(String Name) {
        for (int i = 0; i < NAME.length; i++) {
            if (NAME[i].equals(Name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAccount(String Account) {
        for (int i = 0; i < ACCOUNT.length; i++) {
            if (ACCOUNT[i].equals(Account)) {
                return true;
            }
        }
        return false;
    }

}
