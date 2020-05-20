package cn.kt;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

/**
 * 为 utime/ctime/valid 设置默认值
 *
 * @author chenxiaojing
 */
public class DefaultValuePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        List<IntrospectedColumn> columns = introspectedTable.getAllColumns();

        List<String> insertMethods = new ArrayList<>();
        insertMethods.add("insert");
        insertMethods.add("insertSelective");
        insertMethods.add("batchInsert");
        insertMethods.add("batchInsertSelective");

        List<String> updateMethods = new ArrayList<>();
        updateMethods.add("update");
        updateMethods.add("updateSelective");

        XmlElement xmlElement = document.getRootElement();
        for (Element element : xmlElement.getElements()) {
            if (element instanceof XmlElement) {
                XmlElement xe = (XmlElement) element;
                if (insertMethods.contains(xe.getName())) {
                    // 替换 insert 系列方法
                    checkAndReplaceParam(columns, xe, true);
                } else if (updateMethods.contains(xe.getName())) {
                    // 替换 update 系列方法
                    checkAndReplaceParam(columns, xe, false);
                }
            }
        }
        return true;
    }


    private void checkAndReplaceParam(List<IntrospectedColumn> columns, XmlElement xe, boolean isInsert) {
        for (Element element : xe.getElements()) {
            if (element instanceof XmlElement) {
                checkAndReplaceParam(columns, (XmlElement) element, isInsert);
            }
            if (element instanceof TextElement) {
                TextElement te = (TextElement) element;
                checkAndReplaceParam(columns, te, isInsert);
            }
        }
    }

    private void checkAndReplaceParam(List<IntrospectedColumn> columns, TextElement te, boolean isInsert) {
        String sql = te.getContent();
        for (IntrospectedColumn column : columns) {
            if (("ctime".equals(column.getActualColumnName()) && isInsert) || "utime".equals(column.getActualColumnName())) {
                sql = replace(column, sql, "NOW()");
            }
            if ("valid".equals(column.getActualColumnName())) {
                sql = replace(column, sql, "1");
            }
        }
        try {
            FieldUtils.writeDeclaredField(te, "content", sql, true);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private String replace(IntrospectedColumn column, String sql, String target) {

        String paramStr = MyBatis3FormattingUtilities.getParameterClause(column);
        sql = StringUtils.replace(sql, paramStr, target);
        paramStr = MyBatis3FormattingUtilities.getParameterClause(column, "record.");
        sql = StringUtils.replace(sql, paramStr, target);
        paramStr = MyBatis3FormattingUtilities.getParameterClause(column, "item.");
        sql = StringUtils.replace(sql, paramStr, target);

        return sql;
    }

}