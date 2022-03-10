package com.li2n.im_server.pojo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 好友请求参数
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-2-23 下午 10:15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FriendParams对象", description = "好友请求参数")
public class FriendModel {

    @ApiModelProperty(value = "发送请求的用户名", required = true)
    private String sendUsername;
    @ApiModelProperty(value = "接收请求的用户名", required = true)
    private String receiverUsername;
    @ApiModelProperty(value = "请求内容", required = true)
    private String content;
    @ApiModelProperty(value = "请求备注", required = true)
    private String comment;
    @ApiModelProperty(value = "del/add", required = true)
    private String requestType;
    @ApiModelProperty(value = "true/false", required = true)
    private Boolean result;
    @ApiModelProperty(value = "请求时间", required = true)
    private String sendTime;

}