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
 * 推送通知
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notice_server")
@ApiModel(value="NoticeServer对象", description="推送通知")
public class NoticeServer implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "创建者用户名")
    private String sendUsername;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "类型")
    private String messageContentType;

    @ApiModelProperty(value = "接收的用户")
    private String receiveUsername;

    @ApiModelProperty(value = "推送量")
    private int pushNum;

    @ApiModelProperty(value = "发送时间")
    private LocalDateTime sendTime;


}
