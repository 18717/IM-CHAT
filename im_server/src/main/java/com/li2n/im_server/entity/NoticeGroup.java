package com.li2n.im_server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

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
@ApiModel(value="NoticeGroup", description="好友通知")
public class NoticeGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "发送者用户名")
    @TableField("sender")
    private String sender;

    @ApiModelProperty(value = "接收者用户名")
    @TableField("receiver")
    private String receiver;

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
    @Getter(AccessLevel.NONE)
    @TableField("is_confirm")
    private Integer confirm;

    @ApiModelProperty(value = "是否是添加群聊")
    @Getter(AccessLevel.NONE)
    @TableField("is_join")
    private Integer join;

    @ApiModelProperty(value = "是否是退出群聊")
    @Getter(AccessLevel.NONE)
    @TableField("is_quit")
    private Integer quit;

    @ApiModelProperty(value = "是否是强制退出群聊")
    @Getter(AccessLevel.NONE)
    @TableField("is_force_quit")
    private Integer forceQuit;

    @ApiModelProperty(value = "是否是解散群聊")
    @Getter(AccessLevel.NONE)
    @TableField("is_dismiss")
    private Integer dismiss;

    @ApiModelProperty(value = "是否验证")
    @Getter(AccessLevel.NONE)
    @TableField("is_verified")
    private Integer verified;

    @ApiModelProperty(value = "是否是反馈信息")
    @Getter(AccessLevel.NONE)
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
    private String time;

    @ApiModelProperty(value = "发送时间标记")
    @TableField(exist = false)
    private String flagTime;

    @ApiModelProperty(value = "发送时间")
    @TableField(exist = false)
    private String members;

    @ApiModelProperty(value = "入群结果")
    @TableField(exist = false)
    private String result;

    @TableField(exist = false)
    private User user;

    @TableField(exist = false)
    private GroupInfo groupInfo;

    public Boolean isConfirm() { return this.confirm == 1; }
    public Boolean isJoin() { return this.join == 1; }
    public Boolean isQuit() { return this.quit == 1; }
    public Boolean isForceQuit() { return this.forceQuit == 1; }
    public Boolean isDismiss() { return this.dismiss == 1; }
    public Boolean isVerified() { return this.verified == 1; }
    public Boolean isFlag() { return this.flag == 1; }


}
