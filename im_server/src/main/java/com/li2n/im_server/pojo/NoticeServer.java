package com.li2n.im_server.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "通知类型")
    @TableField("notice_type")
    private String noticeType;

    @ApiModelProperty(value = "创建者用户名")
    @TableField("send_username")
    private String sendUsername;

    @ApiModelProperty(value = "文件名称")
    @TableField("file_name")
    private String fileName;

    @ApiModelProperty(value = "文件链接")
    @TableField("file_url")
    private String fileUrl;

    @ApiModelProperty(value = "内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "内容类型")
    @TableField("content_type")
    private String contentType;

    @ApiModelProperty(value = "接收的用户")
    @TableField("receive_username")
    private String receiveUsername;

    @ApiModelProperty(value = "推送量")
    @TableField("push_num")
    private int pushNum;

    @ApiModelProperty(value = "发送时间")
    @TableField("send_time")
    private LocalDateTime sendTime;


}
