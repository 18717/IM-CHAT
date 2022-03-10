package com.li2n.im_server.pojo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 分页返回对象
 *
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-02-06 02:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "RespPageBeanModel", description = "分页查询结果数据模型")
public class RespPageBeanModel {

    @ApiModelProperty(value = "查询总记录数", required = true)
    private Long total;
    @ApiModelProperty(value = "查询数据", required = true)
    private List<?> data;

    /**
     * 查询错误返回信息
     *
     * @param o
     */
    public RespPageBeanModel(Object o) {
    }
}
