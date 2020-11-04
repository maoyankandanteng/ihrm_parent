package com.ihrm.gate.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class LoginFilter extends ZuulFilter {
    /**
     * 定义过滤器类型
     *  pre :执行路由请求之前
     *  routing：在路由请求时调用
     *  post ： 在routing 和 error过滤器之后执行
     *  error ：处理请求出现异常时执行
     * @return
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * int 过滤器的优先级：数字越小优先级越高
     * @return
     */
    @Override
    public int filterOrder() {
        return 2;
    }

    /**
     * boolean
     *  判断过滤器是否需要执行
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 负责具体的业务逻辑
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        System.out.println("执行方法");
/*
        //1.获取request对象
        RequestContext rc= RequestContext.getCurrentContext();
        HttpServletRequest request = rc.getRequest();
        String token = request.getHeader("Authorization");
        if(token == null || "".equals(token)){
            //没有token信息,需要登陆,拦截
            rc.setSendZuulResponse(false);
            //返回错误码401
            rc.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            //重定向，页面跳转
        }
*/


        return null;
    }
}
