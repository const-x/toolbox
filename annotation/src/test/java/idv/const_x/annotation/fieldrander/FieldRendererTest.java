package idv.const_x.annotation.fieldrander;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2024-02-04
 */
public class FieldRendererTest {

    public static void main(String[] args) {
        TestVO vo = new TestVO();
        vo.setA("aaa");
        vo.setB("bbb");
        vo.setC("ccc");
        vo.setD("ddd");
        vo.setE(null);
        vo.setIsAll(1);
        System.out.println(FieldRendererUtils.render(vo, true, false));
    }

}
