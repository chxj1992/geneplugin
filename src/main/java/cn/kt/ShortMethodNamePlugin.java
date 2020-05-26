package cn.kt;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.util.List;

/**
 * @author chenxiaojing
 */
public class ShortMethodNamePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {

        introspectedTable.setSelectByPrimaryKeyStatementId("getById");
        introspectedTable.setDeleteByPrimaryKeyStatementId("deleteById");
        introspectedTable.setUpdateByPrimaryKeyStatementId("updateById");
        introspectedTable.setUpdateByPrimaryKeySelectiveStatementId("updateByIdSelective");
        introspectedTable.setUpdateByPrimaryKeyWithBLOBsStatementId("updateByIdWithBlobsStatementId");
    }

}