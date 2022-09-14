package com.itheima.reggie.util;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.Arrays;
import java.util.List;

/*
        <!--代码生成器-->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-generator</artifactId>
            <version>3.4.1</version>
        </dependency>

        <!--velocity模板引擎-->
        <dependency>
            <groupId>org.apache.velocity</groupId>
            <artifactId>velocity-engine-core</artifactId>
            <version>2.3</version>
        </dependency>
 */

public class CodeGenerator {
    public static void main(String[] args) {
        //1.获取代码生成器的对象
        AutoGenerator autoGenerator = new AutoGenerator();

        //设置数据库相关配置
        DataSourceConfig dataSource = new DataSourceConfig();
        dataSource.setDriverName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(
                "jdbc:mysql://localhost:3306/reggie?serverTimezone=UTC");
        dataSource.setUsername("root");
        // TODO 改成自己的数据库密码
        dataSource.setPassword("root");
        autoGenerator.setDataSource(dataSource);

        //设置全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        //设置代码生成位置
        globalConfig.setOutputDir(
                System.getProperty("user.dir") + "/src/main/java");
        globalConfig.setOpen(false);    //设置生成完毕后是否打开生成代码所在的目录
        globalConfig.setAuthor("黑马程序员");    //设置作者
        globalConfig.setFileOverride(true);     //设置是否覆盖原始生成的文件
        //globalConfig.setMapperName("%sDao");    //设置数据层接口名，%s为占位符，指代模块名称
        //接口：UserService -> UserServiceImpl
        //IUserService -> UserService
        globalConfig.setServiceName("%sService");
        globalConfig.setIdType(IdType.ASSIGN_ID);   //设置Id生成策略
        autoGenerator.setGlobalConfig(globalConfig);

        //设置包名相关配置
        PackageConfig packageInfo = new PackageConfig();
        packageInfo.setParent("com.itheima.reggie");   //设置生成的包名，与代码所在位置不冲突，二者叠加组成完整路径
        packageInfo.setEntity("pojo");    //设置实体类包名,pojo
        packageInfo.setMapper("mapper");   //设置数据层包名,dao
        autoGenerator.setPackageInfo(packageInfo);

        TemplateConfig templateConfig = new TemplateConfig();
        //不生成UserMapper.xml文件
        templateConfig.setXml(null);
        autoGenerator.setTemplate(templateConfig);

        //策略设置
        StrategyConfig strategyConfig = new StrategyConfig();
        //将表名中的下划线转换成驼峰：表名tb_order_detail -> 实体类：OrderDetail
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        //设置当前参与生成的表名，参数为可变参数
        //strategyConfig.setInclude("tbl_user", "tbl_book");
        //strategyConfig.setTablePrefix("tb_");
        strategyConfig.setRestControllerStyle(true);    //设置是否启用Rest风格
        //strategyConfig.setVersionFieldName("version");  //设置乐观锁字段名
        //strategyConfig.setLogicDeleteFieldName("deleted");  //设置逻辑删除字段名
        strategyConfig.setEntityLombokModel(true);  //设置是否启用lombok

        autoGenerator.setStrategy(strategyConfig);
        //2.执行生成操作
        autoGenerator.execute();


        //设置自动填充
        TableFill createTime = new TableFill("create_time", FieldFill.INSERT);
        TableFill updateTime = new TableFill("update_time", FieldFill.INSERT_UPDATE);
        TableFill createUser = new TableFill("create_user", FieldFill.INSERT);
        TableFill updateUser = new TableFill("update_user", FieldFill.INSERT_UPDATE);

        List<TableFill> fills = Arrays.asList(createTime, updateTime, createUser, updateUser);
        strategyConfig.setTableFillList(fills);

        autoGenerator.setStrategy(strategyConfig);
    }
}
