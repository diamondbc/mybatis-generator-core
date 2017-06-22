package org.mybatis.generator.plugins;

import java.util.List;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

/**
 * 逻辑删除插件
 */
public class LogicDeletePlugin extends PluginAdapter {
    private String deleteKey;
    private String deleteValue;
    private String existValue;


    public LogicDeletePlugin() {
        super();
        deleteKey = properties.get("deleteKey") != null ? properties.get("deleteKey").toString() : "is_deleted";
        deleteValue = properties.get("deleteValue") != null ? properties.get("deleteValue").toString() : "1";
        existValue = properties.get("existValue") != null ? properties.get("existValue").toString() : "0";
    }


    @Override
    public boolean sqlMapExampleWhereClauseElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        XmlElement whereElement = (XmlElement) element.getElements().get(element.getElements().size() - 1);
        whereElement.addElement(initIfDeleteElement());
        return super.sqlMapExampleWhereClauseElementGenerated(element, introspectedTable);
    }


    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        element.addElement(initDeleteElement());
        return super.sqlMapSelectByPrimaryKeyElementGenerated(element, introspectedTable);
    }



    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element,
                    IntrospectedTable introspectedTable) {
        element.addElement(initDeleteElement());
        return super.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(element, introspectedTable);
    }



    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element,
                    IntrospectedTable introspectedTable) {
        element.addElement(initDeleteElement());
        return super.sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(element, introspectedTable);
    }



    @Override
    public boolean sqlMapSelectAllElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        element.addElement(initDeleteElement());
        return super.sqlMapSelectAllElementGenerated(element, introspectedTable);
    }


    /**
     * 主键删除
     */
    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        element.reset();
        element.setName("update");
        element.addAttribute(new Attribute("id", introspectedTable.getDeleteByPrimaryKeyStatementId()));
        String parameterClass;
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            parameterClass = introspectedTable.getPrimaryKeyType();
        } else {
            if (introspectedTable.getPrimaryKeyColumns().size() > 1) {
                parameterClass = "map";
            } else {
                parameterClass = introspectedTable.getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType().toString();
            }
        }
        element.addAttribute(new Attribute("parameterType", parameterClass));
        // 去除注释
        // context.getCommentGenerator().addComment(element);

        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        sb.append(" set " + deleteKey + " = " + deleteValue);
        element.addElement(new TextElement(sb.toString()));

        boolean and = false;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            sb.setLength(0);
            if (and) {
                sb.append("  and ");
            } else {
                sb.append("where ");
                and = true;
            }

            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            element.addElement(new TextElement(sb.toString()));
        }
        return super.sqlMapDeleteByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    /**
     * Example删除
     */
    @Override
    public boolean sqlMapDeleteByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        element.reset();
        element.setName("update");
        String fqjt = introspectedTable.getExampleType();
        element.addAttribute(new Attribute("id", introspectedTable.getDeleteByExampleStatementId()));
        element.addAttribute(new Attribute("parameterType", fqjt));
        // 去除注释
        // context.getCommentGenerator().addComment(element);
        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        sb.append(" set " + deleteKey + " = " + deleteValue);
        element.addElement(new TextElement(sb.toString()));
        element.addElement(getExampleIncludeElement(introspectedTable));
        return super.sqlMapDeleteByPrimaryKeyElementGenerated(element, introspectedTable);
    }



    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    protected XmlElement getExampleIncludeElement(IntrospectedTable introspectedTable) {
        XmlElement ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", "_parameter != null"));

        XmlElement includeElement = new XmlElement("include");
        includeElement.addAttribute(new Attribute("refid", introspectedTable.getExampleWhereClauseId()));
        ifElement.addElement(includeElement);

        return ifElement;
    }

    /**
     * 
     * @desc:删除元素(IF)
     * @author: binchuan.wu
     * @param：@return
     * @return： XmlElement
     * @throws
     */
    private XmlElement initIfDeleteElement() {
        XmlElement deleteElement = new XmlElement("if");
        deleteElement.addAttribute(new Attribute("test", "1=1"));
        deleteElement.addElement(new TextElement("and " + deleteKey + " = " + existValue));
        return deleteElement;
    }

    /**
     * 
     * @desc:删除元素
     * @author: binchuan.wu
     * @param：@return
     * @return： TextElement
     * @throws
     */
    private TextElement initDeleteElement() {
        return new TextElement("and " + deleteKey + " = " + existValue);
    }

}
