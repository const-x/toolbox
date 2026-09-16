package idv.const_x.annotation.fieldrander;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2023-07-14
 */
public interface IFieldValueRenderer {

    public String getRenderedValue(Object target,String fieldName,FieldRenderer annotation,Object originValue);

}
