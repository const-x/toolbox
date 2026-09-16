package idv.const_x.math;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2024-08-23
 */
public class MathUtils {


    public static BigDecimal toBigDecimal(Number num){
        if (num == null) {
            return null;
        }
        if (num instanceof BigDecimal) {
            return (BigDecimal) num;
        }
        if (num instanceof Double) {
            return BigDecimal.valueOf((Double) num);
        }
        if (num instanceof Float) {
            return BigDecimal.valueOf((Float) num);
        }
        return toBigDecimal(String.valueOf(num));
    }

    public static BigDecimal toBigDecimal(String a){
        return toBigDecimal(a,null);
    }

    public static BigDecimal toBigDecimal(String a,String field){
        if (a == null || Objects.equals(a.trim(),"") ) {
            return null;
        }
        BigDecimal b = null;
        try {
            b = new BigDecimal(a);
        }catch (Exception e){
            //更明确区分null和字符串"null"
            if (a.equalsIgnoreCase("null")) {
                a = "\""+a+"\"";
            }
            if (field != null) {
                throw new NumberFormatException(field + "非法:"+a);
            }else{
                throw new NumberFormatException("数值非法:"+a);
            }
        }
        return b;
    }

    public static String toString(BigDecimal num){
        if (num == null) {
            return null;
        }
        return num.toPlainString();
    }

    public static String toString(Number num){
        if (num == null) {
            return null;
        }
        return toString(toBigDecimal(num));
    }

    public static Double toDouble(BigDecimal num){
        if (num == null) {
            return null;
        }
        return num.doubleValue();
    }

    public static Double toDouble(String num){
        if (num == null) {
            return null;
        }
        return Double.parseDouble(num);
    }

    /**
     *
     * @param value
     * @param maxLen
     * @param scale
     * @param tryRepair 尝试修复数据
     * @param toZeroIfErr 当数据无法修复时 是否强制设为0
     * @return
     */
    public static FixResult<String> fixNumString( String value, int maxLen,int scale,boolean tryRepair, boolean toZeroIfErr){
        FixResult<String> result = new FixResult<>();
        result.setOrigin(value);

        String zero = BigDecimal.ZERO.setScale(scale).toPlainString();
        if (Objects.isNull(value)) {
            if (tryRepair || toZeroIfErr){
                result.setFixed(zero);
                result.addMessage("空值置零");
            }else {
                result.addMessage("空值");
            }
            return result;
        }
        boolean fixed = false;


        if (value.contains("'")) {
            value = value.replaceAll("'","");
            if (tryRepair || toZeroIfErr){
                result.addMessage("去单引号");
                fixed  = true;
            }else {
                result.addMessage("含单引号");
            }
        }

        if (value.contains("\"")) {
            value = value.replaceAll("\"","");
            if (tryRepair || toZeroIfErr){
                result.addMessage("去双引号");
                fixed  = true;
            }else {
                result.addMessage("含双引号");
            }
        }

        String trim = value.trim();
        if (!trim.equals(value)) {
            if (tryRepair || toZeroIfErr){
                result.addMessage("去空格");
                value = trim;
                fixed  = true;
            }else {
                result.addMessage("包含空格");
                value = trim;
            }
        }

        if (value.endsWith(".")) {
            if (tryRepair) {
                value = value.substring(0,value.length() -1);
                fixed  = true;
                result.addMessage("'.'结尾");
            }else{
                result.addMessage("'.'结尾");
                value = value.substring(0,value.length() -1);
            }
        }


        BigDecimal decimal = null;
        try {
            decimal = new BigDecimal(value);;
        }catch (NumberFormatException e) {
            if (tryRepair && ZERO_STRINGS.contains(value.toLowerCase())) {
                result.addMessage("自动转0");
                result.setFixed(zero);
                return result;
            }else{
                if (toZeroIfErr) {
                    result.addMessage("异常置零");
                    result.setFixed(zero);
                }else{
                    result.addMessage("非法字符");
                }
                return result;
            }
        }catch (Exception e){
            if (toZeroIfErr) {
                result.addMessage("异常置零");
                result.setFixed(zero);
            }else{
                result.addMessage("转换异常:" + e.getMessage());
            }
            return result;
        }

        if (decimal.scale() > scale) {
            if (tryRepair) {
                value = decimal.setScale(scale,BigDecimal.ROUND_HALF_UP).toPlainString();
                fixed  = true;
                result.addMessage("精度修复");
            }else{
                result.addMessage("未精度处理");
            }
        }

        String s = value;
        int len = s.length();
        if (len > maxLen) {
            if (tryRepair) {
                //尝试移除小数点后数据
                while (s.contains(".")){
                    BigDecimal dec = new BigDecimal(s);
                    dec= dec.setScale(dec.scale() -1,BigDecimal.ROUND_HALF_UP);
                    s = dec.toPlainString();

                    len = s.length();
                    if (len <= maxLen) {
                        result.addMessage("超长缩减");
                        result.setFixed(s);
                        return result;
                    }
                }
            }

            if (toZeroIfErr) {
                value =  "0.0";
                fixed  = true;
                result.addMessage("长度异常置零" );
            }else{
                result.addMessage("长度异常" );
            }
        }

        if (fixed) {
            result.setFixed(value);
        }
        return result;
    }

    /**
     *
     * @param value
     * @param maxLen
     * @param scale
     * @param tryRepair 尝试修复数据
     * @param toZeroIfErr 当数据无法修复时 是否强制设为0
     * @return
     */
    public static FixResult<Double> fixNumDouble( Double value, int maxLen,int scale,boolean tryRepair, boolean toZeroIfErr){
        FixResult<Double> result = new FixResult();
        result.setOrigin(value);

        Double zero = BigDecimal.ZERO.setScale(scale).doubleValue();
        if (Objects.isNull(value)) {
            if (tryRepair || toZeroIfErr){
                result.setFixed(zero);
                result.addMessage("空值置零");
            }else {
                result.addMessage("空值");
            }
            return result;
        }

        boolean fixed = false;

        BigDecimal decimal = null;
        try {
            decimal = new BigDecimal(value);;
        }catch (Exception e){
            if (toZeroIfErr) {
                result.addMessage("异常置零");
                result.setFixed(zero);
            }else{
                result.addMessage("转换异常:" + e.getMessage());
            }
            return result;
        }

        if (decimal.scale() > scale) {
            if (tryRepair) {
                decimal = decimal.setScale(scale,BigDecimal.ROUND_HALF_UP);
                value = decimal.doubleValue();
                fixed  = true;
                result.addMessage("精度修复");
            }else{
                result.addMessage("未精度处理");
            }
        }

        String s = decimal.toPlainString();
        int len = s.length();
        if (len > maxLen) {
            if (tryRepair) {
                //尝试移除小数点后数据
                while (s.contains(".")){
                    BigDecimal dec = new BigDecimal(s);
                    dec= dec.setScale(dec.scale() -1,BigDecimal.ROUND_HALF_UP);
                    s = dec.toPlainString();

                    len = s.length();
                    if (len <= maxLen) {
                        result.addMessage("超长缩减");
                        result.setFixed(dec.doubleValue());
                        return result;
                    }
                }
            }

            if (toZeroIfErr) {
                value =  zero;
                fixed  = true;
                result.addMessage("长度异常置零" );
            }else{
                result.addMessage("长度异常" );
            }
        }

        if (fixed) {
            result.setFixed(value);
        }
        return result;
    }

    public static class FixResult<E>{
        E origin, fixed;
        String      message;

        public E getOrigin() {
            return origin;
        }

        public void setOrigin(E origin) {
            this.origin = origin;
        }

        public E getFixed() {
            return fixed;
        }

        public void setFixed(E fixed) {
            this.fixed = fixed;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void addMessage(String msg){
            if (this.message != null) {
                msg = this.message + ";"+msg;
            }
            setMessage(msg);
        }
    }

    private static List<String> ZERO_STRINGS = Arrays.asList(
            "","null","nul","nvl","nan","undefined"
    );

    private static  void addMessage(FixResult result,String message){
        String string = result.message;
        if (string != null) {
            message = string + ";"+message;
        }
        result.setMessage(message);
    }

}
