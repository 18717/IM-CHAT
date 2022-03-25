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
import java.util.List;

/**
 * <p>
 * 好友通知
 * </p>
 *
 * @author Li2N
 * @since 2022-03-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notice_group")
@ApiModel(value="NoticeGroup对象", description="好友通知")
public class NoticeGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "发送者用户名")
    @TableField("sender_username")
    private String senderUsername;

    @ApiModelProperty(value = "接收者用户名")
    @TableField("receiver_username")
    private String receiverUsername;

    @ApiModelProperty(value = "标题")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "备注内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "群号GID")
    @TableField("gid")
    private String gid;

    @ApiModelProperty(value = "是否确认")
    @TableField("is_confirm")
    private Integer confirm;

    @ApiModelProperty(value = "是否是添加群聊")
    @TableField("is_join")
    private Integer join;

    @ApiModelProperty(value = "是否是退出群聊")
    @TableField("is_quit")
    private Integer quit;

    @ApiModelProperty(value = "是否是强制退出群聊")
    @TableField("is_force_quit")
    private Integer forceQuit;

    @ApiModelProperty(value = "目标是否是离线状态")
    @TableField("is_online")
    private Integer online;

    @ApiModelProperty(value = "是否验证")
    @TableField("is_verified")
    private Integer verified;

    @ApiModelProperty(value = "是否是反馈信息")
    @TableField("is_flag")
    private Integer flag;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "发送时间")
    @TableField(exist = false)
    private String sendTime;

    @ApiModelProperty(value = "发送时间标记")
    @TableField(exist = false)
    private String flagTime;

    @ApiModelProperty(value = "群名")
    @TableField(exist = false)
    private String groupName;

    @TableField(exist = false)
    private UserInfo userInfo;

    @TableField(exist = false)
    private GroupInfo groupInfo;

}
