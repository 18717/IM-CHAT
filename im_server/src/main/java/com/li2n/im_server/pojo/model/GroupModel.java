package com.li2n.im_server.pojo.model;

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
    @ApiModelProperty(value = "申请人", required = true)
    private String sendUsername;
    @ApiModelProperty(value = "群组唯一识别码", required = true)
    private String gid;
    @ApiModelProperty(value = "入群备注", required = true)
    private String comment;
    @ApiModelProperty(value = "申请时间", required = true)
    private String sendTime;
    @ApiModelProperty(value = "是否确认")
    private int confirm;

    public boolean isConfirm() {
        return this.confirm == 1;
    }

}
