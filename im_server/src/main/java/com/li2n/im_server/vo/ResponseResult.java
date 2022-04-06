package com.li2n.im_server.vo;

import com.li2n.im_server.entity.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 公共返回对象
 *
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-1-26 下午 11:27
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "ResponseResult", description = "公共返回对象")
public class ResponseResult {

    @ApiModelProperty(value = "响应状态码", required = true)
    private long code;
    @ApiModelProperty(value = "响应消息", required = true)
    private String message;
    @ApiModelProperty(value = "数据对象", required = true)
    private Object data;

    /**
     * 成功返回结果
     *
     * @param message
     * @author 一杯香梨
     * @date 2022-1-26 下午 11:29
     */
    public static ResponseResult success(String message) {
        return new ResponseResult(200, message, null);
    }

    /**
     * 成功返回结果
     *
     * @param message
     * @param data
     * @author 一杯香梨
     * @date 2022-1-26 下午 11:30
     */
    public static ResponseResult success(String message, Object data) {
        return new ResponseResult(200, message, data);
    }

    /**
     * 成功返回结果
     *
     * @param obj
     * @author 一杯香梨
     * @date 2022-3-17 下午 9:52
     */
    public static ResponseResult success(Object obj) {
        return new ResponseResult(200, null, obj);
    }

    /**
     * 失败返回结果
     *
     * @param message
     * @author 一杯香梨
     * @date 2022-1-26 下午 11:32
     */
    public static ResponseResult error(String message) {
        return new ResponseResult(500, message, null);
    }

    /**
     * 失败返回结果
     *
     * @param message
     * @param data
     * @author 一杯香梨
     * @date 2022-1-26 下午 11:32
     */
    public static ResponseResult error(String message, Object data) {
        return new ResponseResult(500, message, data);
    }
}
