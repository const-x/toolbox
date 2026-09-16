package idv.const_x.annotation.fieldrander;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2023-07-14
 */
public class DefaultValueRenderer implements IFieldValueRenderer{

    @Override
    public String getRenderedValue(Object target, String fieldName,FieldRenderer annotation, Object originValue) {
        if (originValue == null) {
            return null;
        }
        return String.valueOf(originValue);
    }
}
