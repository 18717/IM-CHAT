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
 * 群消息实体类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("group_message")
@ApiModel(value="GroupMessage", description="群消息实体类")
public class GroupMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "群组唯一识别码")
    @TableField("gid")
    private String gid;

    @ApiModelProperty(value = "用户名")
    @TableField("sender")
    private String sender;

    @ApiModelProperty(value = "内容类型")
    @TableField("content_type")
    private String contentType;

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
    private User user;

    @TableField(exist = false)
    private GroupInfo groupInfo;

    @ApiModelProperty(value = "发送时间字符串")
    @TableField(exist = false)
    private String time;

}
