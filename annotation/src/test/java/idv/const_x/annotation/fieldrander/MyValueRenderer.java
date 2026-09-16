package idv.const_x.annotation.fieldrander;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2023-07-14
 */
public class MyValueRenderer implements IFieldValueRenderer{

    @Override
    public String getRenderedValue(Object target, String fieldName,FieldRenderer annotation, Object originValue) {
        return "XXX" + String.valueOf(originValue);
    }
}
