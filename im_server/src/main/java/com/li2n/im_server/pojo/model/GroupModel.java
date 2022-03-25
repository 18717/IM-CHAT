package com.li2n.im_server.pojo.model;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 入群申请参数
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-3-6 下午 9:47
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "GroupModel", description = "入群申请参数")
public class GroupModel {

    @ApiModelProperty(value = "验证码", required = true)
    private String code;
    @ApiModelProperty(value = "发送者头像url", required = true)
    private String avatarUrl;
    @ApiModelProperty(value = "发送者昵称", required = true)
    private String senderNickname;
    @ApiModelProperty(value = "发送者用户名", required = true)
    private String senderUsername;
    @ApiModelProperty(value = "发送者性别", required = true)
    private String gender;
    @ApiModelProperty(value = "接收者用户名", required = true)
    private String receiverUsername;
    @ApiModelProperty(value = "群名", required = true)
    private String groupName;
    @ApiModelProperty(value = "群成员", required = true)
    private String members;
    @ApiModelProperty(value = "群组唯一识别码", required = true)
    private String gid;
    @ApiModelProperty(value = "内容标题", required = true)
    private String title;
    @ApiModelProperty(value = "入群备注", required = true)
    private String content;
    @ApiModelProperty(value = "发送时间", required = true)
    private String sendTime;
    @ApiModelProperty(value = "发送时间标记", required = true)
    private String flagTime;
    @ApiModelProperty(value = "是否同意申请")
    private Integer confirm;
    @ApiModelProperty(value = "反馈标记")
    private Integer flag;
    @ApiModelProperty(value = "业务类型")
    private String businessType;
    @ApiModelProperty(value = "是否验证")
    private Integer verified;

    public boolean isConfirm() {
        return this.confirm == 1;
    }
    public boolean isJoin() { return "join".equals(this.businessType); }
    public boolean isQuit() { return "quit".equals(this.businessType); }
    public boolean isForceQuit() { return "forceQuit".equals(this.businessType); }
    public boolean isDismiss() { return "dismiss".equals(this.businessType); }

}
