package org.mybatis.generator.plugins;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * service生成插件
 */
public class ServicePlugin extends PluginAdapter {

    private FullyQualifiedJavaType slf4jLogger;
    private FullyQualifiedJavaType slf4jLoggerFactory;
    private FullyQualifiedJavaType serviceType;
    private FullyQualifiedJavaType daoType;
    private FullyQualifiedJavaType interfaceType;
    private FullyQualifiedJavaType pojoType;
    private FullyQualifiedJavaType pojoExampleType;
    private FullyQualifiedJavaType listType;
    private FullyQualifiedJavaType autowired;
    private FullyQualifiedJavaType service;
    private FullyQualifiedJavaType returnType;
    private FullyQualifiedJavaType pagination;
    private String servicePack;
    private String serviceImplPack;
    private String targetProject;
    private String targetProjectImpl;
    private String pojoUrl;
    /** 是否添加注解 */
    private boolean enableAnnotation = true;
    private boolean enableInsert = true;
    private boolean enableInsertSelective = true;
    private boolean enableDeleteByPrimaryKey = true;
    private boolean enableDeleteByExample = true;
    private boolean enableUpdateByExample = true;
    private boolean enableUpdateByExampleSelective = true;
    private boolean enableUpdateByPrimaryKey = true;
    private boolean enableUpdateByPrimaryKeySelective = true;
    /** 是否需要实现接口 */
    private boolean needInterface = true;

    public ServicePlugin() {
        super();
        // default is slf4j
        slf4jLogger = new FullyQualifiedJavaType("org.slf4j.Logger");
        slf4jLoggerFactory = new FullyQualifiedJavaType("org.slf4j.LoggerFactory");
        pagination = new FullyQualifiedJavaType("org.mybatis.generator.api.Pagination");
    }

    /**
     * 读取配置文件
     */
    @Override
    public boolean validate(List<String> warnings) {

        String enableAnnotation = properties.getProperty("enableAnnotation");
        String enableInsert = properties.getProperty("enableInsert");
        String enableUpdateByExampleSelective = properties.getProperty("enableUpdateByExampleSelective");
        String enableInsertSelective = properties.getProperty("enableInsertSelective");
        String enableUpdateByPrimaryKey = properties.getProperty("enableUpdateByPrimaryKey");
        String enableDeleteByPrimaryKey = properties.getProperty("enableDeleteByPrimaryKey");
        String enableDeleteByExample = properties.getProperty("enableDeleteByExample");
        String enableUpdateByPrimaryKeySelective = properties.getProperty("enableUpdateByPrimaryKeySelective");
        String enableUpdateByExample = properties.getProperty("enableUpdateByExample");
        if (StringUtility.stringHasValue(enableAnnotation)) {
            this.enableAnnotation = StringUtility.isTrue(enableAnnotation);
        }
        if (StringUtility.stringHasValue(enableInsert)) {
            this.enableInsert = StringUtility.isTrue(enableInsert);
        }
        if (StringUtility.stringHasValue(enableUpdateByExampleSelective)) {
            this.enableUpdateByExampleSelective = StringUtility.isTrue(enableUpdateByExampleSelective);
        }
        if (StringUtility.stringHasValue(enableInsertSelective)) {
            this.enableInsertSelective = StringUtility.isTrue(enableInsertSelective);
        }

        if (StringUtility.stringHasValue(enableUpdateByPrimaryKey)) {
            this.enableUpdateByPrimaryKey = StringUtility.isTrue(enableUpdateByPrimaryKey);
        }

        if (StringUtility.stringHasValue(enableDeleteByPrimaryKey)) {
            this.enableDeleteByPrimaryKey = StringUtility.isTrue(enableDeleteByPrimaryKey);
        }

        if (StringUtility.stringHasValue(enableDeleteByExample)) {
            this.enableDeleteByExample = StringUtility.isTrue(enableDeleteByExample);
        }

        if (StringUtility.stringHasValue(enableUpdateByPrimaryKeySelective)) {
            this.enableUpdateByPrimaryKeySelective = StringUtility.isTrue(enableUpdateByPrimaryKeySelective);
        }

        if (StringUtility.stringHasValue(enableUpdateByExample)) {
            this.enableUpdateByExample = StringUtility.isTrue(enableUpdateByExample);
        }
        this.servicePack = properties.getProperty("targetPackage");
        this.serviceImplPack = properties.getProperty("targetPackageImpl");
        this.targetProject = properties.getProperty("targetProject");
        this.targetProjectImpl = properties.getProperty("targetProjectImpl");
        this.pojoUrl = context.getJavaModelGeneratorConfiguration().getTargetPackage();
        // 分页类实现
        if (StringUtility.stringHasValue(properties.getProperty("paginationClass"))) {
            this.pagination = new FullyQualifiedJavaType(properties.getProperty("paginationClass"));
        }
        // 是否需要接口
        if (StringUtility.stringHasValue(properties.getProperty("needInterface"))) {
            this.needInterface = StringUtility.isTrue(properties.getProperty("needInterface"));
        }
        if (this.enableAnnotation) {
            autowired = new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired");
            service = new FullyQualifiedJavaType("org.springframework.stereotype.Service");
        }
        return true;
    }

    /**
     * 生成类
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> files = new ArrayList<GeneratedJavaFile>();
        // 取Service名称com.XX.service.UserService
        String table = introspectedTable.getBaseRecordType();
        String tableName = table.replaceAll(this.pojoUrl + ".", "");
        // com.XX.mapper.UserMapper
        daoType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
        String impl = "";
        if (needInterface) {
            impl = "Impl";
        }
        interfaceType = new FullyQualifiedJavaType(servicePack + "." + tableName + "Service");
        // com.XX.service.impl.UserServiceImpllogger.info(toLowerCase(daoType.getShortName()));
        serviceType = new FullyQualifiedJavaType(serviceImplPack + "." + tableName + "Service" + impl);
        // com.XX.domain.User
        pojoType = new FullyQualifiedJavaType(pojoUrl + "." + tableName);
        // com.XX.domain.Criteria
        pojoExampleType = new FullyQualifiedJavaType(pojoUrl + "." + tableName + "Example");
        listType = new FullyQualifiedJavaType("java.util.List");
        Interface interface1 = new Interface(interfaceType);
        TopLevelClass topLevelClass = new TopLevelClass(serviceType);
        // 导入必须的类
        if (needInterface) {
            // 接口
            addService(interface1, introspectedTable, tableName, files);
            addImport(interface1);
        }
        // 实现类
        addImport(topLevelClass);
        addServiceImpl(topLevelClass, introspectedTable, tableName, files);
        addLogger(topLevelClass);
        return files;
    }

    /**
     * add interface
     * 
     * @param tableName
     * @param files
     */
    protected void addService(Interface interface1, IntrospectedTable introspectedTable, String tableName,
                    List<GeneratedJavaFile> files) {
        interface1.setVisibility(JavaVisibility.PUBLIC);
        // add method
        Method method = countByExample(introspectedTable, tableName, true);
        interface1.addMethod(method);
        method = selectByPrimaryKey(introspectedTable, tableName, true);
        interface1.addMethod(method);
        method = selectByExample(introspectedTable, tableName, true);
        interface1.addMethod(method);
        method = selectPageByExample(introspectedTable, tableName, true);
        interface1.addMethod(method);
        if (enableDeleteByPrimaryKey) {
            method = getOtherInteger("deleteByPrimaryKey", introspectedTable, tableName, 2, true);
            interface1.addMethod(method);
        }
        if (enableUpdateByPrimaryKeySelective) {
            method = getOtherInteger("updateByPrimaryKeySelective", introspectedTable, tableName, 1, true);
            interface1.addMethod(method);
        }
        if (enableUpdateByPrimaryKey) {
            method = getOtherInteger("updateByPrimaryKey", introspectedTable, tableName, 1, true);
            interface1.addMethod(method);
        }
        if (enableDeleteByExample) {
            method = getOtherInteger("deleteByExample", introspectedTable, tableName, 3, true);
            interface1.addMethod(method);
        }
        if (enableUpdateByExampleSelective) {
            method = getOtherInteger("updateByExampleSelective", introspectedTable, tableName, 4, true);
            interface1.addMethod(method);
        }
        if (enableUpdateByExample) {
            method = getOtherInteger("updateByExample", introspectedTable, tableName, 4, true);
            interface1.addMethod(method);
        }
        if (enableInsert) {
            method = getOtherInsertboolean("insert", introspectedTable, tableName, true);
            interface1.addMethod(method);
        }
        if (enableInsertSelective) {
            method = getOtherInsertboolean("insertSelective", introspectedTable, tableName, true);
            interface1.addMethod(method);
        }
        GeneratedJavaFile file = new GeneratedJavaFile(interface1, targetProject, context.getJavaFormatter());
        files.add(file);
    }

    /**
     * add implements class
     * 
     * @param introspectedTable
     * @param tableName
     * @param files
     */
    protected void addServiceImpl(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String tableName,
                    List<GeneratedJavaFile> files) {
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        if (needInterface) {
            // set implements interface
            topLevelClass.addSuperInterface(interfaceType);
        }
        if (enableAnnotation) {
            topLevelClass.addAnnotation("@Service");
            topLevelClass.addImportedType(service);
        }
        // add import dao
        addField(topLevelClass, tableName);
        // add method
        topLevelClass.addMethod(countByExample(introspectedTable, tableName, false));
        topLevelClass.addMethod(selectByExample(introspectedTable, tableName, false));
        topLevelClass.addMethod(selectPageByExample(introspectedTable, tableName, false));
        topLevelClass.addMethod(selectByPrimaryKey(introspectedTable, tableName, false));

        /**
         * type: pojo 1 ;key 2 ;example 3 ;pojo+example 4
         */
        if (enableDeleteByPrimaryKey) {
            topLevelClass.addMethod(getOtherInteger("deleteByPrimaryKey", introspectedTable, tableName, 2, false));
        }
        if (enableUpdateByPrimaryKeySelective) {
            topLevelClass.addMethod(getOtherInteger("updateByPrimaryKeySelective", introspectedTable, tableName, 1,
                            false));
        }
        if (enableUpdateByPrimaryKey) {
            topLevelClass.addMethod(getOtherInteger("updateByPrimaryKey", introspectedTable, tableName, 1, false));
        }
        if (enableDeleteByExample) {
            topLevelClass.addMethod(getOtherInteger("deleteByExample", introspectedTable, tableName, 3, false));
        }
        if (enableUpdateByExampleSelective) {
            topLevelClass.addMethod(getOtherInteger("updateByExampleSelective", introspectedTable, tableName, 4, false));
        }
        if (enableUpdateByExample) {
            topLevelClass.addMethod(getOtherInteger("updateByExample", introspectedTable, tableName, 4, false));
        }
        if (enableInsert) {
            topLevelClass.addMethod(getOtherInsertboolean("insert", introspectedTable, tableName, false));
        }
        if (enableInsertSelective) {
            topLevelClass.addMethod(getOtherInsertboolean("insertSelective", introspectedTable, tableName, false));
        }
        GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, targetProjectImpl, context.getJavaFormatter());
        files.add(file);
    }

    /**
     * 添加字段
     * 
     * @param topLevelClass
     */
    protected void addField(TopLevelClass topLevelClass, String tableName) {
        // add dao
        Field field = new Field();
        field.setName(toLowerCase(daoType.getShortName())); // set var name
        topLevelClass.addImportedType(daoType);
        field.setType(daoType); // type
        field.setVisibility(JavaVisibility.PRIVATE);
        if (enableAnnotation) {
            field.addAnnotation("@Autowired");
        }
        topLevelClass.addField(field);
    }

    /**
     * 添加方法
     * 
     */
    protected Method selectByPrimaryKey(IntrospectedTable introspectedTable, String tableName, Boolean isInterface) {
        Method method = new Method();
        method.setName("selectByPrimaryKey");
        method.setReturnType(pojoType);
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
            method.addParameter(new Parameter(type, "key"));
        } else {
            for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
                method.addParameter(new Parameter(type, introspectedColumn.getJavaProperty()));
            }
        }
        method.setVisibility(JavaVisibility.PUBLIC);
        if (!isInterface) {
            if (needInterface) {
                method.addAnnotation("@Override");
            }
            StringBuilder sb = new StringBuilder();
            sb.append("return this.");
            sb.append(getDaoShort());
            sb.append("selectByPrimaryKey");
            sb.append("(");
            for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                sb.append(introspectedColumn.getJavaProperty());
                sb.append(",");
            }
            sb.setLength(sb.length() - 1);
            sb.append(");");
            method.addBodyLine(sb.toString());
        }
        return method;
    }

    /**
     * add method
     * 
     */
    protected Method countByExample(IntrospectedTable introspectedTable, String tableName, Boolean isInterface) {
        Method method = new Method();
        method.setName("countByExample");
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.addParameter(new Parameter(pojoExampleType, "example"));
        method.setVisibility(JavaVisibility.PUBLIC);
        if (!isInterface) {
            if (needInterface) {
                method.addAnnotation("@Override");
            }
            StringBuilder sb = new StringBuilder();
            sb.append("return this.");
            sb.append(getDaoShort());
            sb.append("countByExample");
            sb.append("(");
            sb.append("example");
            sb.append(");");
            method.addBodyLine(sb.toString());
        }
        return method;
    }

    /**
     * add method
     * 
     */
    protected Method selectByExample(IntrospectedTable introspectedTable, String tableName, Boolean isInterface) {
        Method method = new Method();
        method.setName("selectByExample");
        method.setReturnType(new FullyQualifiedJavaType("List<" + tableName + ">"));
        method.addParameter(new Parameter(pojoExampleType, "example"));
        method.setVisibility(JavaVisibility.PUBLIC);
        if (!isInterface) {
            if (needInterface) {
                method.addAnnotation("@Override");
            }
            StringBuilder sb = new StringBuilder();
            sb.append("return this.");
            sb.append(getDaoShort());
            if (introspectedTable.hasBLOBColumns()) {
                sb.append("selectByExampleWithoutBLOBs");
            } else {
                sb.append("selectByExample");
            }
            sb.append("(");
            sb.append("example");
            sb.append(");");
            method.addBodyLine(sb.toString());
        }
        return method;
    }

    protected Method selectPageByExample(IntrospectedTable introspectedTable, String tableName, Boolean isInterface) {
        Method method = new Method();
        method.setName("selectPageByExample");
        method.setReturnType(new FullyQualifiedJavaType(pagination.getShortName() + "<" + tableName + ">"));
        method.addParameter(new Parameter(pojoExampleType, "example"));
        method.addParameter(new Parameter(pagination, "pagination", true, tableName));
        method.setVisibility(JavaVisibility.PUBLIC);
        if (!isInterface) {
            if (needInterface) {
                method.addAnnotation("@Override");
            }
            StringBuilder sb = new StringBuilder();
            sb.append("example.setLimit(pagination.getLimit());");
            method.addBodyLine(sb.toString());
            sb = new StringBuilder();
            sb.append("example.setOffset(pagination.getOffset());");
            method.addBodyLine(sb.toString());
            sb = new StringBuilder();
            sb.append("pagination.setData(selectByExample(example));");
            method.addBodyLine(sb.toString());
            sb = new StringBuilder();
            sb.append("pagination.setTotal(countByExample(example));");
            method.addBodyLine(sb.toString());
            sb = new StringBuilder();
            sb.append("return pagination;");
            method.addBodyLine(sb.toString());
        }
        return method;
    }

    /**
     * add method
     * 
     */
    protected Method getOtherInteger(String methodName, IntrospectedTable introspectedTable, String tableName,
                    int type, Boolean isInterface) {
        Method method = new Method();
        method.setName(methodName);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        String params = addParams(introspectedTable, method, type);
        method.setVisibility(JavaVisibility.PUBLIC);
        if (!isInterface) {
            if (needInterface) {
                method.addAnnotation("@Override");
            }
            StringBuilder sb = new StringBuilder();
            sb.append("return this.");
            sb.append(getDaoShort());
            if (introspectedTable.hasBLOBColumns()
                            && (!"updateByPrimaryKeySelective".equals(methodName)
                                            && !"deleteByPrimaryKey".equals(methodName)
                                            && !"deleteByExample".equals(methodName) && !"updateByExampleSelective"
                                                .equals(methodName))) {
                sb.append(methodName + "WithoutBLOBs");
            } else {
                sb.append(methodName);
            }
            sb.append("(");
            sb.append(params);
            sb.append(");");
            method.addBodyLine(sb.toString());
        }
        return method;
    }

    /**
     * add method
     * 
     */
    protected Method getOtherInsertboolean(String methodName, IntrospectedTable introspectedTable, String tableName,
                    Boolean isInterface) {
        Method method = new Method();
        method.setName(methodName);
        method.setReturnType(returnType);
        method.addParameter(new Parameter(pojoType, "record"));
        method.setVisibility(JavaVisibility.PUBLIC);
        if (!isInterface) {
            if (needInterface) {
                method.addAnnotation("@Override");
            }
            StringBuilder sb = new StringBuilder();
            if (returnType == null) {
                sb.append("this.");
            } else {
                sb.append("return this.");
            }
            sb.append(getDaoShort());
            sb.append(methodName);
            sb.append("(");
            sb.append("record");
            sb.append(");");
            method.addBodyLine(sb.toString());
        }
        return method;
    }

    /**
     * type: pojo 1 key 2 example 3 pojo+example 4
     */
    protected String addParams(IntrospectedTable introspectedTable, Method method, int type1) {
        switch (type1) {
            case 1:
                method.addParameter(new Parameter(pojoType, "record"));
                return "record";
            case 2:
                if (introspectedTable.getRules().generatePrimaryKeyClass()) {
                    FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
                    method.addParameter(new Parameter(type, "key"));
                } else {
                    for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                        FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
                        method.addParameter(new Parameter(type, introspectedColumn.getJavaProperty()));
                    }
                }
                StringBuffer sb = new StringBuffer();
                for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                    sb.append(introspectedColumn.getJavaProperty());
                    sb.append(",");
                }
                sb.setLength(sb.length() - 1);
                return sb.toString();
            case 3:
                method.addParameter(new Parameter(pojoExampleType, "example"));
                return "example";
            case 4:
                method.addParameter(0, new Parameter(pojoType, "record"));
                method.addParameter(1, new Parameter(pojoExampleType, "example"));
                return "record, example";
            default:
                break;
        }
        return null;
    }

    protected void addComment(JavaElement field, String comment) {
        StringBuilder sb = new StringBuilder();
        field.addJavaDocLine("/**");
        sb.append(" * ");
        comment = comment.replaceAll("\n", "<br>\n\t * ");
        sb.append(comment);
        field.addJavaDocLine(sb.toString());
        field.addJavaDocLine(" */");
    }

    /**
     * BaseUsers to baseUsers
     * 
     * @param tableName
     * @return
     */
    protected String toLowerCase(String tableName) {
        StringBuilder sb = new StringBuilder(tableName);
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * BaseUsers to baseUsers
     * 
     * @param tableName
     * @return
     */
    protected String toUpperCase(String tableName) {
        StringBuilder sb = new StringBuilder(tableName);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * import must class
     */
    private void addImport(Interface interfaces) {
        interfaces.addImportedType(pojoType);
        interfaces.addImportedType(pojoExampleType);
        interfaces.addImportedType(listType);
        interfaces.addImportedType(pagination);

    }

    private void addImport(TopLevelClass topLevelClass) {
        if (needInterface) {
            topLevelClass.addImportedType(interfaceType);
        }
        topLevelClass.addImportedType(daoType);
        topLevelClass.addImportedType(pojoType);
        topLevelClass.addImportedType(pojoExampleType);
        topLevelClass.addImportedType(listType);
        topLevelClass.addImportedType(slf4jLogger);
        topLevelClass.addImportedType(slf4jLoggerFactory);
        topLevelClass.addImportedType(pagination);
        if (enableAnnotation) {
            topLevelClass.addImportedType(service);
            topLevelClass.addImportedType(autowired);
        }
    }

    /**
     * import logger
     */
    private void addLogger(TopLevelClass topLevelClass) {
        Field field = new Field();
        field.setFinal(true);
        field.setInitializationString("LoggerFactory.getLogger(" + topLevelClass.getType().getShortName() + ".class)"); // set
                                                                                                                        // value
        field.setName("logger"); // set var name
        field.setStatic(true);
        field.setType(new FullyQualifiedJavaType("Logger")); // type
        field.setVisibility(JavaVisibility.PRIVATE);
        topLevelClass.addField(field);
    }

    private String getDaoShort() {
        return toLowerCase(daoType.getShortName()) + ".";
    }

}
