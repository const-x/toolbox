package idv.const_x.annotation.fieldrander;

import idv.const_x.utils.StringExtUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;import java.util.Objects;

/**
 * @Description <pre>
 *     根据@FieldRenderer注解描述 将vo对象翻译成易于业务人员理解的String,一般用于操作日志记录等场景
 *
 * </pre>
 * @Author const.x
 * @Date 2023-07-14
 */
public class FieldRendererUtils {


    private  static  LinkedHashMap<Class<?>, Field[]> FIELD_CACHE = new LinkedHashMap<Class<?>, Field[]>(10, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Class<?>, Field[]> eldest) {
            // 当Map中的元素数量达到maxSize时，返回true将最老的元素移除
            return size() > 10;
        }
    };


    private static Map<String, IFieldValueRenderer> renderers = new HashMap() {{
        put(Int2BooleanRenderer.class.getName(), new Int2BooleanRenderer());
        put(DefaultValueRenderer.class.getName(), new DefaultValueRenderer());
    }};

    public static String render(Object target) {
        return render(target, true, false);
    }

    /**
     * @param target
     * @param ignoreNull
     * @param ignoreUnannotationField 是否忽略未添加@FieldRenderer注解的字段,不忽略则仍以原始字段名和原始值进行展示
     * @return
     * @throws IllegalAccessException
     */
    public static String render(Object target, boolean ignoreNull, boolean ignoreUnannotationField) {
        if (target == null) {
            return "";
        }

        Field[] fields = getFields(target.getClass());
        StringBuilder builder = new StringBuilder();
        for (Field field : fields) {
            String name = field.getName();
            Object o = getValueByFieldName(target, field);
            if (ignoreNull) {
                if (o == null) {
                    continue;
                }
                if (o instanceof String && StringExtUtils.isBlank(o.toString())) {
                    continue;
                }
            }
            String alias = name;
            String randered = String.valueOf(o);
            try {
                FieldRenderer annotation = field.getAnnotation(FieldRenderer.class);
                if (annotation == null && ignoreUnannotationField) {
                    continue;
                }

                IFieldValueRenderer renderer = renderers.get(DefaultValueRenderer.class.getName());
                if (annotation != null) {
                    if (annotation.ignore()) {
                        continue;
                    }
                    if (Objects.equals(annotation.ignoreValue(),String.valueOf(o))) {
                        continue;
                    }
                    if (StringExtUtils.isNotBlank(annotation.value())) {
                        alias = annotation.value();
                    }
                    if (annotation.rendererClass() != null) {
                        String clazz = annotation.rendererClass().getName();
                        if (!renderers.containsKey(clazz)) {
                            IFieldValueRenderer instance = (IFieldValueRenderer) Class.forName(clazz).newInstance();
                            renderers.put(clazz, instance);
                        }
                        renderer = renderers.get(clazz);
                        if (renderer == null) {
                            throw new IllegalAccessException("未知的值渲染器:" + clazz);
                        }
                    }
                    randered = renderer.getRenderedValue(target, name, annotation, o);
                }else {
                    if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    randered = renderer.getRenderedValue(target, name, annotation, o);
                }
                if (randered != null || !ignoreNull) {
                    builder.append(alias).append(":").append(randered).append(";");
                }
            } catch (Exception e) {
                builder.append(alias).append(":").append(String.valueOf(o)).append(";");
            }
        }


        if (builder.length() > 0) {
            builder = builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();


    }


    private static Field[] getFields(Class clazz){
        if (!FIELD_CACHE.containsKey(clazz)) {
            FIELD_CACHE.put(clazz,clazz.getDeclaredFields());
        }
        return FIELD_CACHE.get(clazz);
    }

    private static Object getValueByFieldName(Object target, Field field) {
        Object value = null;
        try {
            if (field != null) {
                if (field.isAccessible()) {
                    value = field.get(target);
                } else {
                    field.setAccessible(true);
                    value = field.get(target);
                    field.setAccessible(false);
                }
            }
        } catch (Exception e) {
        }
        return value;
    }

}