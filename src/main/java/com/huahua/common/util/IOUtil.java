package com.huahua.common.util;


import javax.servlet.ServletInputStream;
import java.io.IOException;

public class IOUtil {
public static String readline(ServletInputStream in,String encode) throws IOException {
    byte[] buf=new byte[8*1012];
    StringBuffer sbuf=new StringBuffer();
    int result;
    do {
        result=in.readLine(buf,0,buf.length);
        if(result !=-1){
            sbuf.append(new String(buf,0,result,encode));
        }
    }while (result!=-1);

    if(sbuf.length()==0){
        return null;
    }
    return sbuf.toString();
}

}
