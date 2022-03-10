package com.li2n.im_server.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 私聊离线消息
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("message_offline")
@ApiModel(value="MessageOffline对象", description="私聊离线消息")
public class MessageOffline implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "发送者昵称")
    @TableField("send_nickname")
    private String sendNickname;

    @ApiModelProperty(value = "发送者用户名")
    @TableField("send_username")
    private String sendUsername;

    @ApiModelProperty(value = "接收者用户名")
    @TableField("receive_username")
    private String receiveUsername;

    @ApiModelProperty(value = "消息内容类型")
    @TableField("message_content_type")
    private String messageContentType;

    @ApiModelProperty(value = "文本内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "文件URL")
    @TableField("file_url")
    private String fileUrl;

    @ApiModelProperty(value = "发送时间")
    @TableField("send_time")
    private LocalDateTime sendTime;


}
