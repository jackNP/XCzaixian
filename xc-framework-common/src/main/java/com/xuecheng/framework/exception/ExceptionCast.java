package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-09-14 17:31
 **/
public class ExceptionCast {
    //通过这个静态方法，获取带有异常代码的异常对象
    public static void cast(ResultCode resultCode){
        throw new CustomException(resultCode);
    }
}
