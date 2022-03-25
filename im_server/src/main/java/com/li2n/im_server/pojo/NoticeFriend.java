package com.li2n.im_server.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.li2n.im_server.pojo.model.FriendModel;
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

    @ApiModelProperty(value = "内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "是否确认")
    @TableField("is_confirm")
    private Integer confirm;

    @ApiModelProperty(value = "是否是添加好友")
    @TableField("is_add")
    private Integer add;

    @ApiModelProperty(value = "是否是删除好友")
    @TableField("is_del")
    private Integer del;

    @ApiModelProperty(value = "目标是否是在线状态")
    @TableField("is_online")
    private Integer online;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "是否验证")
    @TableField("is_verified")
    private Integer verified;

    @ApiModelProperty(value = "结果标记")
    @TableField("is_flag")
    private Integer flag;

    @ApiModelProperty(value = "头像url")
    @TableField("avatar_url")
    private String avatarUrl;

    @ApiModelProperty(value = "发送者昵称")
    @TableField("send_nickname")
    private String sendNickname;

    @ApiModelProperty(value = "发送时间")
    @TableField(exist = false)
    private String sendTime;

    @ApiModelProperty(value = "请求结果")
    @TableField(exist = false)
    private Boolean result;

    @ApiModelProperty(value = "标记时间")
    @TableField(exist = false)
    private String flagTime;


}
