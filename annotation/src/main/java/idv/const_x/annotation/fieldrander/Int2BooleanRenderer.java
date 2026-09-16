package idv.const_x.annotation.fieldrander;

import java.util.Objects;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2023-07-14
 */
public class Int2BooleanRenderer implements IFieldValueRenderer{

    @Override
    public String getRenderedValue(Object target, String fieldName,FieldRenderer annotation, Object originValue) {

        if (originValue == null) {
            return null;
        }
        if (Objects.equals(originValue,1)) {
            return  "true";
        }else {
            return "false";
        }

    }
}
