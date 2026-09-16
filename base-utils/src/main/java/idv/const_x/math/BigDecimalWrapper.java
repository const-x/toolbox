package idv.const_x.math;

import idv.const_x.utils.StringExtUtils;

import java.math.BigDecimal;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2023-03-10
 */
public class BigDecimalWrapper {

    public static BigDecimalWrapper getZero() {
        return new BigDecimalWrapper(BigDecimal.ZERO);
    }

    public static BigDecimalWrapper getOne() {
        return new BigDecimalWrapper(BigDecimal.ONE);
    }

    private BigDecimal curr = null;

    private int scale = 2;
    private int roundingMode = BigDecimal.ROUND_HALF_UP;

    /**
     * 去除BigDecimal对象尾部的多余零
     */
    private boolean stripTrailingZeros = true;

    private boolean nullAsZero = true;

    public boolean isStripTrailingZeros() {
        return stripTrailingZeros;
    }

    public BigDecimalWrapper stripTrailingZeros(boolean stripTrailingZeros) {
        this.stripTrailingZeros = stripTrailingZeros;
        return this;
    }

    public boolean isNullAsZero() {
        return nullAsZero;
    }

    public BigDecimalWrapper nullAsZero(boolean nullAsZero) {
        this.nullAsZero = nullAsZero;
        return this;
    }

    public int getScale() {
        return scale;
    }

    public BigDecimalWrapper scale(int scale) {
        this.scale = scale;
        return this;
    }

    public int getRoundingMode() {
        return roundingMode;
    }

    public BigDecimalWrapper roundingMode(int roundingMode) {
        this.roundingMode = roundingMode;
        return this;
    }

    public BigDecimalWrapper(String curr) {
        this.curr = toBigDecimal(curr);
    }

    public BigDecimalWrapper(Number curr) {
        this.curr = toBigDecimal(curr);
    }


    public BigDecimalWrapper reInit(String curr) {
        this.curr = toBigDecimal(curr);
        return this;
    }

    public BigDecimalWrapper reInit(Number curr) {
        this.curr = toBigDecimal(curr);
        return this;
    }

    public BigDecimalWrapper reInit() {
        this.curr = BigDecimal.ZERO;
        return this;
    }


    public BigDecimalWrapper add(String ... a) {
        if (a != null) {
            for (String s : a) {
                curr = curr.add(toBigDecimal(s));
            }
        }
        return this;
    }

    public BigDecimalWrapper add(Number ... a) {
        if (a != null) {
            for (Number s : a) {
                curr = curr.add(toBigDecimal(s));
            }
        }
        return this;
    }

    public BigDecimalWrapper subtract(String ... a) {
        if (a != null) {
            for (String s : a) {
                curr = curr.subtract(toBigDecimal(s));
            }
        }
        return this;
    }

    public BigDecimalWrapper subtract(Number ... a) {
        if (a != null) {
            for (Number s : a) {
                curr = curr.subtract(toBigDecimal(s));
            }
        }
        return this;
    }

    public BigDecimalWrapper multiply(String a) {
        curr = curr.multiply(toBigDecimal(a));
        return this;
    }

    public BigDecimalWrapper multiply(Number a) {
        curr = curr.multiply(toBigDecimal(a));
        return this;
    }

    public BigDecimalWrapper divide(String a) {
        BigDecimal divide = toBigDecimal(a);
        divide(divide);
        return this;
    }

    public BigDecimalWrapper divide(Number a) {
        BigDecimal divide = toBigDecimal(a);
        if (divide.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("除数不能为0");
        }
        curr = curr.divide(divide, 9, roundingMode);
        return this;
    }


    public String getString() {
        return get().toPlainString();
    }

    public double getDouble() {
        return get().doubleValue();
    }

    public float getFloat() {
        return get().floatValue();
    }

    public int getInteger() {
        return get().intValue();
    }

    public long getLong() {
        return get().longValue();
    }

    public BigDecimal get() {
        return get(null);
    }

    public String getString(Integer scale) {
        return get(scale).toPlainString();
    }

    public double getDouble(Integer scale) {
        return get(scale).doubleValue();
    }

    public float getFloat(Integer scale) {
        return get(scale).floatValue();
    }

    public BigDecimal get(Integer scale) {
        if (curr == null) {
            if (nullAsZero) {
                curr = BigDecimal.ZERO;
            } else {
                throw new NumberFormatException("数值非法:null");
            }
        }
        if (scale == null) {
            scale = this.scale;
        }
        BigDecimal decimal = curr;
        if (decimal.scale() > scale) {
            decimal = decimal.setScale(scale, roundingMode);
        }
        if (stripTrailingZeros) {
            decimal = decimal.stripTrailingZeros();
        }
        return decimal;
    }


    public boolean equals(Number b) {
        return equals(toBigDecimal(b));
    }

    public boolean equals(String b) {
        return equals(toBigDecimal(b));
    }

    public boolean equals(BigDecimalWrapper b) {
        return equals(b.get());
    }

    public boolean isZero() {
        return equals(BigDecimal.ZERO);
    }

    public boolean greatThanZero() {
        return get().compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean equalsOrGreatThanZero() {
        return get().compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean lessThanZero() {
        return get().compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean equalsOrLessThanZero() {
        return get().compareTo(BigDecimal.ZERO) <= 0;
    }

    private boolean equals(BigDecimal b) {
        if (b == null) {
            if (nullAsZero) {
                b = BigDecimal.ZERO;
            } else {
                throw new NumberFormatException("数值非法:null");
            }
        }
        return get().compareTo(b) == 0;
    }

    private BigDecimal toBigDecimal(Number a) {
        if (a == null) {
            if (nullAsZero) {
                return BigDecimal.ZERO;
            } else {
                throw new NumberFormatException("数值非法:null");
            }
        }
        if (a instanceof BigDecimal) {
            return (BigDecimal) a;
        }
        if (a instanceof Double) {
            return BigDecimal.valueOf((Double) a);
        }
        return toBigDecimal(String.valueOf(a));
    }

    private BigDecimal toBigDecimal(String a) {
        if (StringExtUtils.isBlank(a) && nullAsZero) {
            return BigDecimal.ZERO;
        }
        BigDecimal b = null;
        try {
            b = new BigDecimal(a);
        } catch (Exception e) {
            //更明确区分null和字符串"null"
            throw new NumberFormatException("数值非法:" + (a == null ? null : "\"" + a + "\""));
        }
        return b;
    }

}
