package com.xxx.api.apiscaffold.model;

import java.io.Serializable;

public class Role implements Serializable {
    private Integer id;

    private String roleName;

    private String roleNameZh;

    private static final long serialVersionUID = 1L;

    public Role(Integer id, String roleName, String roleNameZh) {
        this.id = id;
        this.roleName = roleName;
        this.roleNameZh = roleNameZh;
    }

    public Role() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName == null ? null : roleName.trim();
    }

    public String getRoleNameZh() {
        return roleNameZh;
    }

    public void setRoleNameZh(String roleNameZh) {
        this.roleNameZh = roleNameZh == null ? null : roleNameZh.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", roleName=").append(roleName);
        sb.append(", roleNameZh=").append(roleNameZh);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}