package ltd.xiaomizha.xuyou.common.handler;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.LogicFieldEnum;
import ltd.xiaomizha.xuyou.common.enums.TimeFieldEnum;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyBatisMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入填充...");
        log.debug("MetaObject class: {}", metaObject.getOriginalObject().getClass().getName());

        // // 创建时间
        // if (metaObject.hasSetter("createdAt")) {
        //     this.strictInsertFill(metaObject, "createAt", LocalDateTime.class, LocalDateTime.now());
        // }
        //
        // // 更新时间
        // if (metaObject.hasSetter("updatedAt")) {
        //     this.strictInsertFill(metaObject, "updateAt", LocalDateTime.class, LocalDateTime.now());
        // }
        //
        // // 逻辑删除字段
        // if (metaObject.hasSetter("deleted")) {
        //     this.strictInsertFill(metaObject, "deleted", Boolean.class, false);
        // }

        // 打印所有可用的 setter 方法
        for (String field : metaObject.getSetterNames()) {
            log.debug("Available setter: {}", field);
        }
        TimeFieldEnum.createTimeFields().forEach(timeField -> {
            String fieldName = timeField.getFieldName();
            log.debug("检查创建时间字段: {}", fieldName);
            if (metaObject.hasSetter(fieldName)) {
                log.debug("字段 {} 存在, 进行填充", fieldName);
                this.strictInsertFill(metaObject, fieldName, DateTime.class, DateUtil.date());
            } else {
                log.debug("字段 {} 不存在", fieldName);
            }
        });
        TimeFieldEnum.updateTimeFields().forEach(timeField -> {
            if (metaObject.hasSetter(timeField.getFieldName())) {
                this.strictInsertFill(metaObject, timeField.getFieldName(), DateTime.class, DateUtil.date());
            }
        });
        if (metaObject.hasSetter(LogicFieldEnum.DELETED.getFieldName())) {
            this.strictInsertFill(metaObject, LogicFieldEnum.DELETED.getFieldName(), Boolean.class, false);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新填充...");

        // // 更新时间
        // if (metaObject.hasSetter("updatedAt")) {
        //     this.strictUpdateFill(metaObject, "updateAt", LocalDateTime.class, LocalDateTime.now());
        // }

        TimeFieldEnum.updateTimeFields().forEach(timeField -> {
            if (metaObject.hasSetter(timeField.getFieldName())) {
                this.strictUpdateFill(metaObject, timeField.getFieldName(), DateTime.class, DateUtil.date());
            }
        });
    }
}