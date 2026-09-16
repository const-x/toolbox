package idv.const_x.annotation.fieldrander;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2023-07-14
 */
public class TestVO {

    public static final String AAA = "aaa";
    @FieldRenderer(value = "别名A",ignoreValue = "aaa")
    private String a;

    @FieldRenderer(value = "别名B",rendererClass =MyValueRenderer.class)
    private String b;

    @FieldRenderer(ignore = true)
    private String c;

    private String d;

    @FieldRenderer()
    private String e;

    @FieldRenderer(value="是否All",rendererClass=Int2BooleanRenderer.class)
    private Integer isAll;


    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public Integer getIsAll() {
        return isAll;
    }

    public void setIsAll(Integer isAll) {
        this.isAll = isAll;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }
}
