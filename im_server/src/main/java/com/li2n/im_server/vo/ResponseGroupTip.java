package com.li2n.im_server.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 群组数量响应提示
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-3-6 下午 10:54
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "ResponseGroupTip", description = "创建群响应信息")
public class ResponseGroupTip {

    @ApiModelProperty(value = "新建群的Gid", required = true)
    private String gid;
    @ApiModelProperty(value = "已创建的群数量", required = true)
    private int foundNum;
    @ApiModelProperty(value = "已加入的群数量", required = true)
    private int joinNum;
    @ApiModelProperty(value = "剩余可创建的群数量", required = true)
    private int remainderFoundNum;
    @ApiModelProperty(value = "剩余可加入的群数量", required = true)
    private int remainderJoinNum;

}
