package com.li2n.im_server.pojo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 修改密码数据模型
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-2-15 上午 5:24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PasswordModel", description = "修改密码数据模型")
public class EditPasswordModel {

    @ApiModelProperty(value = "需要修改密码的用户", required = true)
    private String username;
    @ApiModelProperty(value = "旧密码", required = true)
    private String old;
    @ApiModelProperty(value = "新密码", required = true)
    private String fresh;
}
