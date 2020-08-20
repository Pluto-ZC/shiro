package com.example.config;

import com.example.pojo.User;
import com.example.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

//自定义realm  extends AuthorizingRealm
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("执行了 => 授权doGetAuthorizationInfo");

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //info.addStringPermission("user:add");

        //拿到当前登录的这个对象  是通过下面的doget方法传过来的
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User) subject.getPrincipal();

        //当前用户
        System.out.println(currentUser);
        System.out.println(principals); //返回的是一个集合
        System.out.println(principals.getPrimaryPrincipal()); //集合中的第一个

        //设置当前用户的权限
        info.addStringPermission(currentUser.getPerms());

        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("执行了 => 认证doGetAuthenticationInfo");

        //获取当前用户  
        UsernamePasswordToken userToken = (UsernamePasswordToken) token;

        //连接真是的数据库
        User user = userService.queryUserByName(userToken.getUsername());

        if(user == null){
            return null; //抛出异常 UnknownAccountException
        }

        //问题：密码为验证就将用户存储在session  应该在密码验证之后再存储
        Subject currentSubject = SecurityUtils.getSubject();
        Session session = currentSubject.getSession();
        session.setAttribute("loginUser", user);


        //密码认证 shiro做  user 将用户传给上面方法doGetAuthorizationInfo
        return new SimpleAuthenticationInfo(user,user.getPwd(),"");
    }
}
