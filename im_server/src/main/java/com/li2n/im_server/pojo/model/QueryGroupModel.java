package com.li2n.im_server.pojo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 群查询参数
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-3-6 下午 11:19
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "QueryGroupModel", description = "群查询数据模型")
public class QueryGroupModel {

    @ApiModelProperty(value = "Gid", required = true)
    private String gid;
    @ApiModelProperty(value = "群名", required = true)
    private String groupName;
    @ApiModelProperty(value = "群主名", required = true)
    private String masterUsername;

}
