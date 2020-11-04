package com.ihrm.system.shiro.realm;

import com.ihrm.common.shiro.realm.IhrmRealm;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.User;
import com.ihrm.domain.system.response.ProfileResult;
import com.ihrm.system.service.PermissionService;
import com.ihrm.system.service.UserService;
import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRealm extends IhrmRealm {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token= (UsernamePasswordToken) authenticationToken;
        String mobile = token.getUsername();
        String password = new String(token.getPassword());

        User user = userService.findByMobile(mobile);
        if(user !=null && user.getPassword().equals(password)){
            ProfileResult profileResult = null;

            if ("user".equals(user.getLevel())) {
                profileResult = new ProfileResult(user);
            } else {
                Map<String, Object> map = new HashMap<>();
                if ("coAdmin".equals(user.getLevel())) {
                    map.put("enVisible", "1");
                }
                List<Permission> list = permissionService.findSearch(map);
                profileResult = new ProfileResult(user, list);
            }
            return new SimpleAuthenticationInfo(profileResult,password,getName());
        }
        return null;//抛出异常
    }
}
