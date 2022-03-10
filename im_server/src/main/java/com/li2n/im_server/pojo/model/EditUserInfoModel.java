package com.li2n.im_server.pojo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 修改用户信息数据模型
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-3-6 上午 11:50
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "EditUserInfoModel", description = "修改用户信息数据模型")
public class EditUserInfoModel {

    @ApiModelProperty(value = "用户名", required = true)
    String username;
    @ApiModelProperty(value = "UID", required = true)
    String uid;
    @ApiModelProperty(value = "登录密码", required = true)
    String password;
    @ApiModelProperty(value = "社交昵称", required = true)
    String nickname;
    @ApiModelProperty(value = "邮箱", required = true)
    String email;
    @ApiModelProperty(value = "头像URL", required = true)
    String avatar;
    @ApiModelProperty(value = "性别", required = true)
    String gender;
    @ApiModelProperty(value = "管理员", required = true)
    int admin;
    @ApiModelProperty(value = "是否禁用", required = true)
    int disable;

}
