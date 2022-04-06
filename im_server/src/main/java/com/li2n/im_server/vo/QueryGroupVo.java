package com.li2n.im_server.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 查询群聊参数实体类
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-3-6 下午 11:19
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "QueryGroupVo", description = "查询群聊参数实体类")
public class QueryGroupVo {

    @ApiModelProperty(value = "Gid", required = true)
    private String gid;
    @ApiModelProperty(value = "群名", required = true)
    private String groupName;
    @ApiModelProperty(value = "群主名", required = true)
    private String leader;

}
