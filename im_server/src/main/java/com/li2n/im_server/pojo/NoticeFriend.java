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
 * 好友通知
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notice_friend")
@ApiModel(value="NoticeFriend对象", description="好友通知")
public class NoticeFriend implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableField("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "发送者用户名")
    @TableField("send_username")
    private String sendUsername;

    @ApiModelProperty(value = "接收者用户名")
    @TableField("receive_username")
    private String receiveUsername;

    @ApiModelProperty(value = "标题")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "通知类型")
    private String noticeType;

    @ApiModelProperty(value = "内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "是否确认")
    @TableField("is_confirm")
    private int confirm;

    @ApiModelProperty(value = "是否是添加好友")
    @TableField("is_add")
    private int add;

    @ApiModelProperty(value = "是否是删除好友")
    @TableField("is_del")
    private int del;

    @ApiModelProperty(value = "目标是否是在线状态")
    @TableField("is_online")
    private int online;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;


}
