package com.li2n.im_server.pojo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 查询用户数据模型
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-2-25 上午 1:04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "QueryUserModel", description = "查询用户数据模型")
public class QueryUserModel {

    @ApiModelProperty(value = "用户唯一识别码", required = true)
    private String uid;
    @ApiModelProperty(value = "昵称", required = true)
    private String nickname;
    @ApiModelProperty(value = "邮箱", required = true)
    private String email;

}
