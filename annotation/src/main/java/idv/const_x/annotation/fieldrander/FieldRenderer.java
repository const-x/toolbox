package idv.const_x.annotation.fieldrander;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段渲染描述
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldRenderer {

    /**
     * 中文别名
     * @return
     */
    String value() default "";

    /**
     * 忽略当前字段
     * @return
     */
    boolean ignore() default false;


    /**
     * 当为指定值时忽略当前字段
     * @return
     */
    String ignoreValue() default "default_un_ignore_string";

    /**
     * 使用指定渲染器渲染字段值
     * @return
     */
    Class<? extends IFieldValueRenderer> rendererClass() default DefaultValueRenderer.class;

}
