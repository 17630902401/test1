package com.itheima.test;

import com.itheima.reggie.util.SMSUtils;

public class Test {
    public static void main(String[] args) {
        SMSUtils.sendMessage("阿里云短信测试",
                "SMS_154950909",
                "177********", "666888");
    }
}
