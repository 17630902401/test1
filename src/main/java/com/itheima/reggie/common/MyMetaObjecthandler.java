package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjecthandler implements MetaObjectHandler {
    /**
     * 插入操作，自动填充
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        //insertFill执行时机：当咱们调用xxMapper.insert()
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.getOriginalObject().toString());
        //不要写成数据库字段：create_time
        //metaObject.setValue("createTime", LocalDateTime.now());
        //metaObject.setValue("updateTime",LocalDateTime.now());

        //在此类中无法获取到request, session对象，因此无法获取到当前登录用户ID
        //metaObject.setValue("createUser",BaseContext.getCurrentId());
        //metaObject.setValue("updateUser",BaseContext.getCurrentId());
        this.fillStrategy(metaObject, "createTime", LocalDateTime.now());
        this.fillStrategy(metaObject, "updateTime", LocalDateTime.now());
        this.fillStrategy(metaObject, "createUser", BaseContext.getCurrentId());
        this.fillStrategy(metaObject, "updateUser", BaseContext.getCurrentId());
    }

    /**
     * 更新操作，自动填充
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        //updateFill执行时机：xxMapper.updateById(), xxMapper.update()
        log.info("公共字段自动填充[update]...");
        log.info(metaObject.getOriginalObject().toString());
        //获取当前线程ID
        long id = Thread.currentThread().getId();
        log.info("线程id为：{}",id);
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
}
