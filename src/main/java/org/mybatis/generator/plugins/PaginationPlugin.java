package org.mybatis.generator.plugins;

import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.PrimitiveTypeWrapper;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * 分页插件
 */
public class PaginationPlugin extends PluginAdapter {
    private String offset;
    private String limit;


    public PaginationPlugin() {
        super();
        offset = properties.get("offset") != null ? properties.get("offset").toString() : "offset";
        limit = properties.get("limit") != null ? properties.get("limit").toString() : "limit";
        
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // add field, getter, setter,constructor for limit clause
        addConstructor(topLevelClass, introspectedTable);
        addLimit(topLevelClass, introspectedTable, offset);
        addLimit(topLevelClass, introspectedTable, limit);
        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

    /**
     * @desc:添加构造方法
     * @author: binchuan.wu
     * @param：@param topLevelClass
     * @param：@param introspectedTable
     * @return： void
     * @throws
     */
    private void addConstructor(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Method method = new Method();
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getExampleType());
        method.setName(type.getShortName());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setConstructor(true);
        method.addParameter(new Parameter(PrimitiveTypeWrapper.getIntegerInstance(), offset));
        method.addParameter(new Parameter(PrimitiveTypeWrapper.getIntegerInstance(), limit));
        method.addBodyLine("this." + offset + "=" + offset + ";");
        method.addBodyLine("this." + limit + "=" + limit + ";");
        topLevelClass.addMethod(method);
    }

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
                    IntrospectedTable introspectedTable) {
        XmlElement isNotNullElement = new XmlElement("if"); //
        isNotNullElement.addAttribute(new Attribute("test", limit + " != null and " + limit + " >= 0"));  
        isNotNullElement.addElement(new TextElement("limit #{" + offset + "} , #{" + limit + "}"));
        element.addElement(isNotNullElement);
        return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    private void addLimit(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String name) {
        CommentGenerator commentGenerator = context.getCommentGenerator();
        Field field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        // field.setType(FullyQualifiedJavaType.getIntInstance());
        field.setType(PrimitiveTypeWrapper.getIntegerInstance());
        field.setName(name);
        // field.setInitializationString("-1");
        commentGenerator.addFieldComment(field, introspectedTable);
        topLevelClass.addField(field);
        char c = name.charAt(0);
        String camel = Character.toUpperCase(c) + name.substring(1);
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("set" + camel);
        method.addParameter(new Parameter(PrimitiveTypeWrapper.getIntegerInstance(), name));
        method.addBodyLine("this." + name + "=" + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(PrimitiveTypeWrapper.getIntegerInstance());
        method.setName("get" + camel);
        method.addBodyLine("return " + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }

    /**
     * This plugin is always valid - no properties are required
     */
    public boolean validate(List<String> warnings) {
        return true;
    }

}
