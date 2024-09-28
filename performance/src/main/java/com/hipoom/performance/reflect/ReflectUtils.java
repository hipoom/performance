package com.hipoom.performance.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author ZhengHaiPeng
 * @since 2024/9/28 00:31
 */
@SuppressWarnings("unused, CallToPrintStackTrace")
public class ReflectUtils {

    /* ======================================================= */
    /* Public Methods                                          */
    /* ======================================================= */

    /**
     * 从一个类中获取指定名称的 Field
     *
     * @param clazz     目标类
     * @param fieldName 目标字段的名称
     */
    @Nullable
    public static Field getFieldFromClass(@NonNull Class<?> clazz, @NonNull String fieldName) {
        Field field;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }

        field.setAccessible(true);
        return field;
    }

    /**
     * 从一个类中获取指定名称的field
     *
     * @param className 目标类名
     * @param fieldName 目标字段的名称
     */
    @Nullable
    public static Field getFieldFromClass(@NonNull String className, @NonNull String fieldName) {
        Class<?> clazz = getClass(className);
        if (clazz == null) {
            return null;
        }

        return getFieldFromClass(clazz, fieldName);
    }

    /**
     * 从 clazz 类型的 obj 对象中，获取一个名为 fieldName 的字段对应的值。
     * 如果字段是静态变量，obj 可以为 null。
     */
    @Nullable
    public static Object getValue(@NonNull Class<?> clazz, @NonNull String fieldName, @Nullable Object obj) {
        Field field = getFieldFromClass(clazz, fieldName);
        if (field == null) {
            return null;
        }

        field.setAccessible(true);

        Object value;
        try {
            value = field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        return value;
    }

    /**
     * 从 className 类型的 obj 对象中，获取一个名为 fieldName 的字段对应的值。
     * 如果字段是静态变量，obj 可以为 null。
     */
    public static Object getValue(@NonNull String className, @NonNull String fieldName, @Nullable Object obj) {
        Class<?> clazz = getClass(className);

        if (clazz == null) {
            return null;
        }

        return getValue(clazz, fieldName, obj);
    }

    /**
     * 设置 className 类型的 obj 对象的 fieldName 字段的值为 newValue
     */
    public static void setValue(@NonNull String className, @NonNull String fieldName, Object newValue, Object obj) {
        Class<?> clazz = getClass(className);

        if (clazz == null) {
            return;
        }

        setValue(clazz, fieldName, newValue, obj);
    }

    /**
     * 设置 clazz 类型的 obj 对象的 fieldName 字段的值为 newValue
     */
    public static void setValue(@NonNull Class<?> clazz, @NonNull String fieldName, Object newValue, Object obj) {
        Field field = getFieldFromClass(clazz, fieldName);
        if (field == null) {
            return;
        }
        try {
            // 打开访问
            boolean isAccessible = field.isAccessible();
            if (!isAccessible) {
                field.setAccessible(true);
            }

            field.set(obj, newValue);

            // 关闭访问
            if (!isAccessible) {
                field.setAccessible(false);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取 clazz 类中，参数列表为 parameterTypes、方法名为 methodName 的方法
     */
    public static Method getMethodFromClass(@NonNull Class<?> clazz, @NonNull String methodName, Class<?>... parameterTypes) {
        Method method;
        try {
            method = clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }

        return method;
    }

    /**
     * 获取 className 类中，参数列表为 parameterTypes、方法名为 methodName 的方法
     */
    public static Method getMethodFromClass(@NonNull String className, @NonNull String methodName, Class<?>... parameterTypes) {
        Class<?> clazz = getClass(className);
        if (clazz == null) {
            return null;
        }

        return getMethodFromClass(clazz, methodName, parameterTypes);
    }

    /**
     * 获取类名对应的类
     */
    public static Class<?> getClass(@NonNull String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
