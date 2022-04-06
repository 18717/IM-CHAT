package com.li2n.im_server.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用户好友业务参数实体类
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-2-23 下午 10:15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FriendVoX", description = "用户好友业务参数实体类")
public class FriendVoX {

    @ApiModelProperty(value = "头像Url", required = true)
    private String avatar;
    @ApiModelProperty(value = "发送者的昵称", required = true)
    private String nickname;
    @ApiModelProperty(value = "发送者的用户名", required = true)
    private String sender;
    @ApiModelProperty(value = "接收者的用户名", required = true)
    private String receiver;
    @ApiModelProperty(value = "标题", required = true)
    private String title;
    @ApiModelProperty(value = "内容", required = true)
    private String content;
    @ApiModelProperty(value = "业务类型", required = true)
    private String businessType;
    @ApiModelProperty(value = "结果", required = true)
    private Boolean result;
    @ApiModelProperty(value = "发送时间", required = true)
    private String sendTime;
    @ApiModelProperty(value = "反馈标记", required = true)
    private Integer flag;
    @ApiModelProperty(value = "是否已验证", required = true)
    private Integer verified;
    @ApiModelProperty(value = "标记时间", required = true)
    private String flagTime;

    public Boolean isAdd() { return "add".equals(this.businessType); }
    public Boolean isDel() { return "del".equals(this.businessType); }

}
