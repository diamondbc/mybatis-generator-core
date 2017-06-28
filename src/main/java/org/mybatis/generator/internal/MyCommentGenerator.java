/**
 * Copyright 2006-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mybatis.generator.internal;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * MyCommentGenerator
 *
 */
public class MyCommentGenerator extends DefaultCommentGenerator {



    /**
     * 类注释
     */
    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        addClassComment(innerClass, introspectedTable, false);
    }

    /**
     * 类注释
     */
    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        String shortName = innerClass.getType().getShortName();
        innerClass.addJavaDocLine("/**");
        innerClass.addJavaDocLine(" * " + shortName);
        innerClass.addJavaDocLine(" * 表名:" + introspectedTable.getFullyQualifiedTable());
        innerClass.addJavaDocLine(" * " + getDateString());
        innerClass.addJavaDocLine(" */");
    }

    /**
     * 表属性注释
     */
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        StringBuffer sb = new StringBuffer();
        field.addJavaDocLine("/**");
        sb.append(" * 列名:");
        sb.append(introspectedColumn.getActualColumnName());
        field.addJavaDocLine(sb.toString());
        field.addJavaDocLine(" * 备注:" + introspectedColumn.getRemarks());
        field.addJavaDocLine(" */");
    }

    /**
     * 普通属性备注
     */
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {}


    /**
     * getter注释
     */
    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable,
                    IntrospectedColumn introspectedColumn) {}

    /**
     * setter注释
     */
    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable,
                    IntrospectedColumn introspectedColumn) {}

    /**
     * 具体方法注释
     */
    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {}

    /**
     * xml注释
     */
    @Override
    public void addComment(XmlElement xmlElement) {}
}
