package cn.kt;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * A MyBatis Generator plugin to use Lombok's annotations.
 * For example, use @Data annotation instead of getter ands setter.
 *
 * @author Paolo Predonzani (http://softwareloop.com/)
 */
public class MapperCommentPlugin extends PluginAdapter {

    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd").withZone(ZoneId.systemDefault());

    private String author;

    @Override
    public boolean validate(List<String> list) {
        author = getProperties().getProperty("author");
        return true;
    }

    @Override
    public boolean clientGenerated(
            Interface interfaze,
            TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable
    ) {
        interfaze.addJavaDocLine("/**");
        interfaze.addJavaDocLine(" * " + introspectedTable.getRemarks() + "(" + introspectedTable.getFullyQualifiedTable().getIntrospectedTableName() + ") Mapper 类");
        interfaze.addJavaDocLine(" * @author " + author);
        interfaze.addJavaDocLine(" * @since " + DATE_FORMATTER.format(Instant.now()));
        interfaze.addJavaDocLine(" */");
        return true;
    }

}