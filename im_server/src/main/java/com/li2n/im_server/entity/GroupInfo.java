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
import java.util.List;

/**
 * <p>
 * 群基本信息
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("group_info")
@ApiModel(value="GroupInfo", description="群基本信息")
public class GroupInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "群组唯一识别码")
    @TableField("gid")
    private String gid;

    @ApiModelProperty(value = "群名")
    @TableField("group_name")
    private String groupName;

    @ApiModelProperty(value = "群主用户名")
    @TableField("leader")
    private String leader;

    @ApiModelProperty(value = "群描述")
    @TableField("group_describe")
    private String groupDescribe;

    @ApiModelProperty(value = "成员")
    @TableField("members")
    private String members;

    @ApiModelProperty(value = "成员数")
    @TableField("member_num")
    private int memberNum;

    @ApiModelProperty(value = "逻辑删除")
    @TableField("is_deleted")
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "群主信息")
    @TableField(exist = false)
    private User user;

    @ApiModelProperty(value = "验证码")
    @TableField(exist = false)
    private String code;

    @ApiModelProperty(value = "验证码")
    @TableField(exist = false)
    private String time;

    @ApiModelProperty(value = "群成员用户名数组")
    @TableField(exist = false)
    private List<String> memberArr;

}
