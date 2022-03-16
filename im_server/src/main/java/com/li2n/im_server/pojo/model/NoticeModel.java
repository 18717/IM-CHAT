package com.li2n.im_server.pojo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 通知数据模型 用户接收和返回前端的数据
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-3-14 下午 4:02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "NoticeModel", description = "通知数据模型")
public class NoticeModel {

    @ApiModelProperty(value = "推送对象")
    private String push;
    @ApiModelProperty(value = "接收者用户名")
    private String receiveUsername;
    @ApiModelProperty(value = "标题")
    private String title;
    @ApiModelProperty
    private String fileName;
    @ApiModelProperty(value = "文件链接")
    private String fileUrl;
    @ApiModelProperty(value = "推送内容")
    private String content;
    @ApiModelProperty(value = "推送时间")
    private String pushTime;
    @ApiModelProperty(value = "推送量")
    private int pushNum;

}
