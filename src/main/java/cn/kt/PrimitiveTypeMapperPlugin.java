package cn.kt;

import java.lang.reflect.Field;
import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * A MyBatis Generator plugin to use Lombok's annotations.
 * For example, use @Data annotation instead of getter ands setter.
 *
 * @author Paolo Predonzani (http://softwareloop.com/)
 */
public class PrimitiveTypeMapperPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean clientGenerated(
            Interface interfaze,
            TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable
    ) {
        FullyQualifiedJavaType intType = FullyQualifiedJavaType.getIntInstance();

        for (Method method : interfaze.getMethods()) {
            for (Parameter parameter : method.getParameters()) {
                if ("Integer".equals(parameter.getType().getShortName())) {
                    try {
                        Field field = Parameter.class.getDeclaredField("type");
                        field.setAccessible(true);
                        field.set(parameter, intType);
                    }
                    catch (NoSuchFieldException | IllegalAccessException ignore) {
                    }
                }
            }
        }

        return true;
    }

}