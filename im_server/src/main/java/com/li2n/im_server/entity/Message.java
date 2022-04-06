package com.li2n.im_server.entity;

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
@TableName("message")
@ApiModel(value="Message", description="私聊消息汇总")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "消息记录标识")
    @TableField("mkey")
    private String mkey;

    @ApiModelProperty(value = "发送者用户名")
    @TableField("sender")
    private String sender;

    @ApiModelProperty(value = "接收者用户名")
    @TableField("receiver")
    private String receiver;

    @ApiModelProperty(value = "消息内容类型")
    @TableField("content_type")
    private String contentType;

    @ApiModelProperty(value = "文本内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "文件URL")
    @TableField("file_url")
    private String fileUrl;

    @ApiModelProperty(value = "发送时间")
    @TableField("send_time")
    private LocalDateTime sendTime;

    @TableField("is_self")
    private Integer self;

    @TableField(exist = false)
    private User user;

    @TableField(exist = false)
    private String time;

}
