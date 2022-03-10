package com.li2n.im_server.pojo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 公共返回对象
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-1-26 下午 11:27
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "RespBeanModel", description = "自定义返回结果数据模型")
public class RespBeanModel {

    @ApiModelProperty(value = "响应状态码", required = true)
    private long code;
    @ApiModelProperty(value = "响应消息", required = true)
    private String message;
    @ApiModelProperty(value = "数据对象", required = true)
    private Object obj;

    /**
     * 成功返回结果
     * @param message
     * @author 一杯香梨
     * @date 2022-1-26 下午 11:29
     */
    public static RespBeanModel success(String message) {
        return new RespBeanModel(200, message, null);
    }

    /**
     * 成功返回结果
     * @param message 
     * @param obj
     * @author 一杯香梨
     * @date 2022-1-26 下午 11:30
     */
    public static RespBeanModel success(String message, Object obj) {
        return new RespBeanModel(200, message, obj);
    }

    /**
     * 失败返回结果
     * @param message 
     * @author 一杯香梨
     * @date 2022-1-26 下午 11:32
     */
    public static RespBeanModel error(String message) {
        return new RespBeanModel(500, message, null);
    }
    
    /**
     * 失败返回结果
     * @param message 
     * @param obj
     * @author 一杯香梨
     * @date 2022-1-26 下午 11:32
     */
    public static RespBeanModel error(String message, Object obj) {
        return new RespBeanModel(500, message, obj);
    }
}
