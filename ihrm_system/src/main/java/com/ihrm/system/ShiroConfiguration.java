package com.ihrm.system;

import com.ihrm.common.shiro.realm.IhrmRealm;
import com.ihrm.common.shiro.session.CustomSessionManager;
import com.ihrm.system.shiro.realm.UserRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfiguration {

    @Bean
    public IhrmRealm getRealm(){
        return new UserRealm();
    }

    @Bean
    public SecurityManager getSecurityManager(IhrmRealm realm){
        DefaultWebSecurityManager securityManager=new DefaultWebSecurityManager(realm);
        securityManager.setSessionManager(sessionManager());
        securityManager.setCacheManager(cacheManager());
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
        //创建过滤器工厂
        ShiroFilterFactoryBean filterFactoryBean=new ShiroFilterFactoryBean();
        filterFactoryBean.setSecurityManager(securityManager);
        filterFactoryBean.setLoginUrl("/autherror?code=1");
        filterFactoryBean.setUnauthorizedUrl("/autherror?code=2");

        //设置过滤器集合
        Map<String,String> filterMap=new LinkedHashMap<>();
//        filterMap.put("/user/home","anon");
        filterMap.put("/system/login","anon");//匿名访问
        filterMap.put("/system/faceLogin/**","anon");//匿名访问
        filterMap.put("/autherror","anon");//匿名访问
        //具有某种权限才能访问
//        filterMap.put("/user/home","perms[user-home]");//如果不具备指定权限,跳转到UnauthorizedUrl
        filterMap.put("/**","authc");
//        filterMap.put("/user/**","authc");



        filterFactoryBean.setFilterChainDefinitionMap(filterMap);
        return filterFactoryBean;
    }

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    public RedisManager redisManager(){
        RedisManager redisManager=new RedisManager();
        redisManager.setHost(host);
        redisManager.setPort(port);
        redisManager.setPassword(password);
        return redisManager;
    }

    public RedisSessionDAO redisSessionDAO(){
        RedisSessionDAO sessionDAO=new RedisSessionDAO();
        sessionDAO.setRedisManager(redisManager());
        return sessionDAO;
    }

    //会话管理器
    public DefaultWebSessionManager sessionManager(){
        CustomSessionManager sessionManager=new CustomSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO());
        sessionManager.setSessionIdCookieEnabled(false);//禁用cookie
        sessionManager.setSessionIdUrlRewritingEnabled(false);//禁用url重写,url;jsessionid=id
        return sessionManager;
    }

    public RedisCacheManager cacheManager(){
        RedisCacheManager cacheManager=new RedisCacheManager();
        cacheManager.setRedisManager(redisManager());
        return cacheManager;
    }


    //配置shiro注解支持
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

}
