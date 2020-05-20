package cn.kt;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;


/**
 * @author chenxiaojing
 */
public class BatchInsertPlugin extends PluginAdapter {


    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    /**
     * 修改Mapper类
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        // 设置需要import的类
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
        importedTypes.add(FullyQualifiedJavaType.getNewListInstance());
        importedTypes.add(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
        importedTypes.add(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));
        FullyQualifiedJavaType ibsreturnType = FullyQualifiedJavaType.getIntInstance();

        addBatchInsertMethod(interfaze, introspectedTable, importedTypes, ibsreturnType);
//        addBatchInsertSelectiveMethod(interfaze, introspectedTable, importedTypes, ibsreturnType);

        return true;
    }


    /**
     * 修改Mapper.xml
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        addBatchInsertXml(document, introspectedTable);
//        addBatchInsertSelectiveXml(document, introspectedTable);
        return true;
    }


    private void addBatchInsertSelectiveMethod(Interface interfaze, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> importedTypes, FullyQualifiedJavaType ibsreturnType) {
        // BatchInsertSelective
        Method batchInsertSelectiveMethod = new Method();
        // 1.设置方法可见性
        batchInsertSelectiveMethod.setVisibility(JavaVisibility.PUBLIC);
        // 2.设置返回值类型 int类型
        batchInsertSelectiveMethod.setReturnType(ibsreturnType);
        // 3.设置方法名
        batchInsertSelectiveMethod.setName("batchInsertSelective");
        // 4.设置参数列表
        FullyQualifiedJavaType paramTypeSelective = FullyQualifiedJavaType.getNewListInstance();
        FullyQualifiedJavaType paramListTypeSelective = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        paramTypeSelective.addTypeArgument(paramListTypeSelective);
        batchInsertSelectiveMethod.addParameter(new Parameter(paramTypeSelective, "records", "@Param(\"records\")"));
        batchInsertSelectiveMethod.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "columns", "@Param(\"columns\")", true));
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(batchInsertSelectiveMethod);
    }

    private void addBatchInsertMethod(Interface interfaze, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> importedTypes, FullyQualifiedJavaType ibsreturnType) {
        // BatchInsert
        Method batchInsertMethod = new Method();
        // 1.设置方法可见性
        batchInsertMethod.setVisibility(JavaVisibility.PUBLIC);
        // 2.设置返回值类型 int类型
        batchInsertMethod.setReturnType(ibsreturnType);
        // 3.设置方法名
        batchInsertMethod.setName("batchInsert");
        // 4.设置参数列表
        FullyQualifiedJavaType paramType = FullyQualifiedJavaType.getNewListInstance();
        FullyQualifiedJavaType paramListType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        paramType.addTypeArgument(paramListType);
        batchInsertMethod.addParameter(new Parameter(paramType, "records"));
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(batchInsertMethod);
    }



    private void addBatchInsertXml(Document document, IntrospectedTable introspectedTable) {
        // <insert ...
        XmlElement insertBatchElement = new XmlElement("insert");
        insertBatchElement.addAttribute(new Attribute("id", "batchInsert"));
        insertBatchElement.addAttribute(new Attribute("parameterType", "java.util.List"));
        XmlElement valueTrimElement = new XmlElement("trim");
        valueTrimElement.addAttribute(new Attribute("prefix", " ("));
        valueTrimElement.addAttribute(new Attribute("suffix", ")"));
        valueTrimElement.addAttribute(new Attribute("suffixOverrides", ","));
        XmlElement columnTrimElement = new XmlElement("trim");
        columnTrimElement.addAttribute(new Attribute("prefix", "("));
        columnTrimElement.addAttribute(new Attribute("suffix", ")"));
        columnTrimElement.addAttribute(new Attribute("suffixOverrides", ","));
        List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
        for (IntrospectedColumn introspectedColumn : columns) {
            String columnName = introspectedColumn.getActualColumnName();
            columnTrimElement.addElement(new TextElement(columnName + ","));
            valueTrimElement.addElement(new TextElement("#{item." + introspectedColumn.getJavaProperty() + ",jdbcType=" + introspectedColumn.getJdbcTypeName() + "},"));
        }
        XmlElement foreachElement = new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("collection", "list"));
        foreachElement.addAttribute(new Attribute("index", "index"));
        foreachElement.addAttribute(new Attribute("item", "item"));
        foreachElement.addAttribute(new Attribute("separator", ","));
        insertBatchElement.addElement(new TextElement("insert into " + introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime()));
        insertBatchElement.addElement(columnTrimElement);
        insertBatchElement.addElement(new TextElement(" values "));
        foreachElement.addElement(valueTrimElement);
        insertBatchElement.addElement(foreachElement);
        document.getRootElement().addElement(insertBatchElement);
    }


    private void addBatchInsertSelectiveXml(Document document, IntrospectedTable introspectedTable) {
        // <insert ...
        XmlElement insertBatchElement = new XmlElement("insert");
        insertBatchElement.addAttribute(new Attribute("id", "batchInsertSelective"));
        insertBatchElement.addAttribute(new Attribute("parameterType", "map"));
        XmlElement foreachColumn = new XmlElement("foreach");
        foreachColumn.addAttribute(new Attribute("collection", "columns"));
        foreachColumn.addAttribute(new Attribute("index", "index"));
        foreachColumn.addAttribute(new Attribute("item", "item"));
        foreachColumn.addAttribute(new Attribute("separator", ","));
        foreachColumn.addAttribute(new Attribute("open", "("));
        foreachColumn.addAttribute(new Attribute("close", ")"));
        foreachColumn.addElement(new TextElement("${item}"));
        insertBatchElement.addElement(new TextElement("insert into " + introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime()));
        insertBatchElement.addElement(foreachColumn);
        insertBatchElement.addElement(new TextElement(" values "));
        XmlElement valueTrimElement = new XmlElement("trim");
        valueTrimElement.addAttribute(new Attribute("prefix", " ("));
        valueTrimElement.addAttribute(new Attribute("suffix", ")"));
        valueTrimElement.addAttribute(new Attribute("suffixOverrides", ","));
        XmlElement foreachColumnForValue = new XmlElement("foreach");
        foreachColumnForValue.addAttribute(new Attribute("collection", "columns"));
        foreachColumnForValue.addAttribute(new Attribute("index", "index"));
        foreachColumnForValue.addAttribute(new Attribute("item", "column"));
        List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
        for (IntrospectedColumn introspectedColumn : columns) {
            String columnName = introspectedColumn.getActualColumnName();
            XmlElement check = new XmlElement("if");
            check.addAttribute(new Attribute("test", "'" + columnName + "' == column"));
            check.addElement(new TextElement("#{record." + introspectedColumn.getJavaProperty() + ",jdbcType=" + introspectedColumn.getJdbcTypeName() + "},"));
            foreachColumnForValue.addElement(check);
        }
        valueTrimElement.addElement(foreachColumnForValue);
        XmlElement foreachElement = new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("collection", "records"));
        foreachElement.addAttribute(new Attribute("index", "index"));
        foreachElement.addAttribute(new Attribute("item", "record"));
        foreachElement.addAttribute(new Attribute("separator", ","));
        foreachElement.addElement(valueTrimElement);
        insertBatchElement.addElement(foreachElement);
        document.getRootElement().addElement(insertBatchElement);
    }

}