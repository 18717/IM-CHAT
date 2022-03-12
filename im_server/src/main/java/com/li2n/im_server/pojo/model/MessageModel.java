package com.li2n.im_server.pojo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 消息实体类数据模型 用户接收前端传到后端的数据
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-3-12 下午 11:55
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MessageModel", description = "接收消息数据模型")
public class MessageModel {

    @ApiModelProperty(value = "发送者用户名")
    private String sendUsername;

    @ApiModelProperty(value = "接收者用户名")
    private String receiveUsername;

    @ApiModelProperty(value = "消息内容类型")
    private String messageContentType;

    @ApiModelProperty(value = "文本内容")
    private String content;

    @ApiModelProperty(value = "文件URL")
    private String fileUrl;

    @ApiModelProperty(value = "发送时间")
    private String sendTime;

    @ApiModelProperty(value = "该消息是否是发送出去的")
    private String self;

}
