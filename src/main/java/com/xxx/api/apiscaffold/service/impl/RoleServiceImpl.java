package com.xxx.api.apiscaffold.service.impl;

import com.xxx.api.apiscaffold.dao.RoleMapper;
import com.xxx.api.apiscaffold.dao.UserRoleMapper;
import com.xxx.api.apiscaffold.model.Role;
import com.xxx.api.apiscaffold.model.UserRoleKey;
import com.xxx.api.apiscaffold.model.example.RoleExample;
import com.xxx.api.apiscaffold.model.example.UserRoleExample;
import com.xxx.api.apiscaffold.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2020/4/24 14:23
 * @extra code change the world
 * @description
 */
@Service("roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleMapper mapper;

    @Autowired
    UserRoleMapper userRoleMapper;

    @Override
    public List<Role> getRoleByUser(int userId) {
        UserRoleExample userRoleExample = new UserRoleExample();
        userRoleExample.createCriteria().andUserIdEqualTo(userId);
        List<UserRoleKey> userRoleKeys = userRoleMapper.selectByExample(userRoleExample);
        if (userRoleKeys == null || userRoleKeys.isEmpty()) {
            return null;
        }

        List<Integer> roleIds = userRoleKeys.stream().map(item -> item.getRoleId()).collect(Collectors.toList());
        RoleExample roleExample = new RoleExample();
        roleExample.createCriteria().andIdIn(roleIds);

        return mapper.selectByExample(roleExample);
    }
}
