package com.itheima.reggie.common;

import com.itheima.reggie.pojo.Employee;
import lombok.Data;

/**
 * 通用返回结果，服务端响应的数据最终都会封装成此对象
 * @param <T>
 */
@Data
public class R<T> {// T:type类型, E:element元素
    private Integer code; //编码：1成功，0和其它数字为失败
    private String msg; //错误信息
    private T data; //数据

    //success:成功
    //static: 静态方法，可以直接通过类名.方法名访问，R.succes()
    //在方法返回值前边添加泛型，代表泛型方法, R.<List>succes(), R.<Employee>succes()
    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }


    public static void main(String[] args) {
        R result = R.success(new Employee());
        System.out.println(result);

        result = R.error("查询失败");
        System.out.println(result);
    }
}
