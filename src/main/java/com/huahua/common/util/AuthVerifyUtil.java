package com.huahua.common.util;

import com.java.util.CommonUtil;
import com.java.util.StringUtil;

import java.util.Arrays;
import java.util.Map;

public class AuthVerifyUtil {
    public  boolean verifyMD5Sign(String secretKey,String tokenID, String path, Map<String,String> paramsMap,String body){
        //MD5验证
        if(StringUtil.isEmpty(secretKey)){
            return false;
        }
        String sign=paramsMap.get("sign");
        if(StringUtil.isEmpty(sign))
            return false;
        String[] keys=paramsMap.keySet().toArray(new String[0]);
        //重新排序
        Arrays.sort(keys);
        StringBuilder strBuilder =new StringBuilder();
        for(String key:keys){
            String value=paramsMap.get(key);
            //忽略sign和value值为空的参数
            if(key.equals("sign")||StringUtil.isEmpty(value)){
                continue;
            }
            strBuilder.append(key).append(value);
        }
        //post请求
        if(!StringUtil.isEmpty(body)){
            strBuilder.append(body);
        }
        //加密
        byte[] bytes= CommonUtil.encryptionMD5(strBuilder.toString(),secretKey);
        //二进制转化为大写十六进制
        String tarSign=byte2hex(bytes);
        return sign.equals(tarSign);
    }

    private String byte2hex(byte[] bytes){
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }


}
