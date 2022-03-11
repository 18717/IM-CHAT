package com.li2n.im_server.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * <p>
 * 用户基本信息
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_info")
@ApiModel(value="UserInfo对象", description="用户基本信息")
public class UserInfo implements Serializable, UserDetails {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "计数id")
    @TableId(value = "id", type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "用户唯一识别码")
    @TableField("uid")
    private String uid;

    @ApiModelProperty(value = "登录名")
    @TableField("username")
    private String username;

    @ApiModelProperty(value = "登录密码")
    @TableField("pwd")
    private String password;

    @ApiModelProperty(value = "头像URL")
    @TableField("avatar")
    private String avatar;

    @ApiModelProperty(value = "昵称")
    @TableField("nickname")
    private String nickname;

    @ApiModelProperty(value = "性别")
    @TableField("gender")
    private String gender;

    @ApiModelProperty(value = "邮箱")
    @TableField("email")
    private String email;

    @ApiModelProperty(value = "注册时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "管理员")
    @Getter(value = AccessLevel.NONE)
    @TableField("is_admin")
    private int admin;

    @ApiModelProperty(value = "登录状态")
    @Getter(value = AccessLevel.NONE)
    @TableField("is_login")
    private int login;

    @ApiModelProperty(value = "账号状态")
    @Getter(value = AccessLevel.NONE)
    @TableField("is_disable")
    private int disable;

    @ApiModelProperty(value = "逻辑删除")
    @Getter(value = AccessLevel.NONE)
    @TableField("is_deleted")
    private int deleted;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.disable == 1;
    }

    public boolean isAdmin() {
        return this.admin == 1;
    }

    public boolean isLogin() {
        return this.login == 1;
    }

    public boolean isDeleted() {
        return this.deleted == 1;
    }


}
