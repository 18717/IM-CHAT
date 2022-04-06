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
 * @since 2022-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notice_friend")
@ApiModel(value = "NoticeFriend", description = "好友通知")
public class NoticeFriend implements Serializable {

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

    @ApiModelProperty(value = "内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "是否确认")
    @TableField("is_confirm")
    private Integer confirm;

    @ApiModelProperty(value = "是否是添加好友")
    @Getter(AccessLevel.NONE)
    @TableField("is_add")
    private Integer add;

    @ApiModelProperty(value = "是否是删除好友")
    @Getter(AccessLevel.NONE)
    @TableField("is_del")
    private Integer del;

    @ApiModelProperty(value = "是否验证")
    @TableField("is_verified")
    private Integer verified;

    @ApiModelProperty(value = "验证过后的消息")
    @TableField("is_flag")
    private Integer flag;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "请求结果")
    @TableField(exist = false)
    private Boolean result;

    @ApiModelProperty(value = "标记时间")
    @TableField(exist = false)
    private String flagTime;

    @ApiModelProperty(value = "发送时间")
    @TableField(exist = false)
    private String time;

    @ApiModelProperty(value = "接收者用户信息")
    @TableField(exist = false)
    private User user;

    public Boolean isAdd() {
        return this.add.equals(1);
    }

    public Boolean isDel() {
        return this.del.equals(1);
    }


}
