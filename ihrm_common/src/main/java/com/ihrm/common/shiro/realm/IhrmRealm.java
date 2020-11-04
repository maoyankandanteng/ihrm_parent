package com.ihrm.common.shiro.realm;

import com.ihrm.domain.system.response.ProfileResult;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Set;

public class IhrmRealm extends AuthorizingRealm {

    public void setName(String name) {
        super.setName("IhrmRealm");
    }


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取已经认证的安全数据
        ProfileResult  profileResult = (ProfileResult) principalCollection.getPrimaryPrincipal();
        //获取用户的权限信息
        SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();

        Set<String> apis = (Set<String>) profileResult.getRoles().get("apis");
        info.addStringPermissions(apis);
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        return null;
    }
}
