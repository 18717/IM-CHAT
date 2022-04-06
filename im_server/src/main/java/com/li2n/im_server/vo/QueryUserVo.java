package com.li2n.im_server.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 查询用户参数实体类
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-2-25 上午 1:04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "QueryUserVo", description = "查询用户参数实体类")
public class QueryUserVo {

    @ApiModelProperty(value = "用户唯一识别码", required = true)
    private String uid;
    @ApiModelProperty(value = "昵称", required = true)
    private String nickname;
    @ApiModelProperty(value = "邮箱", required = true)
    private String email;

}
