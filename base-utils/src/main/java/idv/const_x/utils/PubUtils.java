package idv.const_x.utils;

import java.util.function.Supplier;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2024-01-16
 */
public class PubUtils {


    /**
     * 形如 a.getB().getC().getD(),中间任何一个对象为空 则返回指定默认值
     * @param supplier
     * @param defaultVal
     * @return
     * @param <T>
     */
    public static  <T> T safeGet(Supplier<T> supplier,T defaultVal){
        try{
            return supplier.get();
        }catch (NullPointerException e){
            return defaultVal;
        }
    }

    public static  <T> T safeGet(Supplier<T> supplier){
        try{
            return supplier.get();
        }catch (NullPointerException e){
            return null;
        }
    }
}
