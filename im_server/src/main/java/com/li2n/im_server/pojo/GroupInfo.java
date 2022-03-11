package com.li2n.im_server.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 群基本信息
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("group_info")
@ApiModel(value="GroupInfo对象", description="群基本信息")
public class GroupInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "群组唯一识别码")
    private String gid;

    @ApiModelProperty(value = "群名")
    private String groupName;

    @ApiModelProperty(value = "群主用户名")
    private String masterUsername;

    @ApiModelProperty(value = "群描述")
    private String groupDescribe;

    @ApiModelProperty(value = "成员")
    private String members;

    @ApiModelProperty(value = "成员")
    private int memberNum;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

}
