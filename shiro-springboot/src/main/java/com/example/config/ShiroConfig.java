package com.example.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    //3、ShiroFilterFactoryBean
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager defaultWebSecurityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        //设置安全管理器
        factoryBean.setSecurityManager(defaultWebSecurityManager);

        //添加shiro的内置过滤器
        /**
         * anon:无需认证就可以访问
         * authc:必须认证了才能访问
         * user:必须拥有记住我功能才能访问
         * perms:拥有对谋个资源的权限才能访问
         * role:拥有耨个角色权限才能访问
         */

        //拦截
        Map<String, String> filterMap = new LinkedHashMap<>();
        //授权，正常情况下，没有授权会跳转到未授权页面
        //先设置有权限限制的  后设置没有权限限制的
        filterMap.put("/user/add", "perms[user:add]");
        filterMap.put("/user/update", "perms[user:update]");
        filterMap.put("/user/*", "authc");

        factoryBean.setFilterChainDefinitionMap(filterMap);

        //设置登录的请求
        factoryBean.setLoginUrl("/toLogin");

        //未授权页面
        factoryBean.setUnauthorizedUrl("/noauth");

        return factoryBean;
    }


    //2、DefaultWebSecurityManager Qualifier明确具体的实现类  默认是方法名  有name属性写name属性
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //关联UserRealm
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    //1、创建realm对象，需要自定义类  交给spring容器管理  默认是方法名userRealm
    @Bean
    public UserRealm userRealm() {
        return new UserRealm();
    }

    //整合ShiroDialect：用来整合shiro thymeleaf
    @Bean
    public ShiroDialect getShiroDialect(){
        return new ShiroDialect();
    }
}
