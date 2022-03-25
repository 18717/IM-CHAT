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
 * 群消息汇总
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("message_group")
@ApiModel(value="MessageGroup对象", description="群消息汇总")
public class MessageGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "群组唯一识别码")
    @TableField("gid")
    private String gid;

    @ApiModelProperty(value = "用户名")
    @TableField("sender_username")
    private String senderUsername;

    @ApiModelProperty(value = "内容类型")
    @TableField("message_content_type")
    private String messageContentType;

    @ApiModelProperty(value = "内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "文件URL")
    @TableField("file_url")
    private String fileUrl;

    @ApiModelProperty(value = "发送时间")
    @TableField("send_time")
    private LocalDateTime sendTime;

    @ApiModelProperty(value = "是否是当前登录用户发送出去的")
    @TableField("is_self")
    private Integer self;

    @TableField(exist = false)
    private UserInfo userInfo;

    @ApiModelProperty(value = "发送时间字符串")
    @TableField(exist = false)
    private String sendTimeStr;

}
