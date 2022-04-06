package com.li2n.im_server.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 消息实体类
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-3-12 下午 11:55
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MessageVoX", description = "消息实体类")
public class MessageVoX {

    @ApiModelProperty(value = "昵称")
    private String sendNickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "发送者用户名")
    private String sendUsername;

    @ApiModelProperty(value = "接收者用户名")
    private String receiveUsername;

    @ApiModelProperty(value = "群聊唯一识别码")
    private String gid;

    @ApiModelProperty(value = "群主")
    private String groupMaster;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "消息内容类型")
    private String messageContentType;

    @ApiModelProperty(value = "文本内容")
    private String content;

    @ApiModelProperty(value = "文件URL")
    private String fileUrl;

    @ApiModelProperty(value = "发送时间")
    private String sendTimeStr;

    @ApiModelProperty(value = "该消息是否是发送出去的")
    private Integer self;

}
