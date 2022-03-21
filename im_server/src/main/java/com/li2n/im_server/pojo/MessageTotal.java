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
 * 私聊消息汇总
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("message_total")
@ApiModel(value="MessageTotal对象", description="私聊消息汇总")
public class MessageTotal implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "发送者用昵称")
    @TableField("send_nickname")
    private String sendNickname;

    @ApiModelProperty(value = "消息记录标识")
    @TableField("mkey")
    private String mkey;

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

    @ApiModelProperty(value = "是否是在线消息")
    @TableField("is_online")
    private int online;

    @ApiModelProperty(value = "该消息是否是发送出去的")
    @TableField("is_self")
    private int self;

}
