package com.xxx.api.apiscaffold.service;

import com.xxx.api.apiscaffold.model.Role;

import java.util.List;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2020/4/24 14:23
 * @extra code change the world
 * @description
 */
public interface RoleService {

    List<Role> getRoleByUser(int userId);

}
