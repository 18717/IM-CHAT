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
 * 私聊消息汇总
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("message_total")
@ApiModel(value="MessageTotal对象", description="私聊消息汇总")
public class MessageTotal implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "消息记录标识")
    private String mkey;

    @ApiModelProperty(value = "发送者用户名")
    private String sendUsername;

    @ApiModelProperty(value = "接收者用户名")
    private String receiveUsername;

    @ApiModelProperty(value = "消息内容类型")
    private String messageContentType;

    @ApiModelProperty(value = "文本内容")
    private String content;

    @ApiModelProperty(value = "文件URL")
    private String fileUrl;

    @ApiModelProperty(value = "发送时间")
    private LocalDateTime sendTime;

    @ApiModelProperty(value = "是否是在线消息")
    private Integer online;

    @ApiModelProperty(value = "该消息是否是发送出去的")
    private Integer self;

}
