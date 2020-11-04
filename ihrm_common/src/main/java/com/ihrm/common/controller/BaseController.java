package com.ihrm.common.controller;

import com.ihrm.domain.system.response.ProfileResult;
import io.jsonwebtoken.Claims;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseController {

    protected HttpServletRequest request;

    protected HttpServletResponse response;

    protected String companyId;

    protected String companyName;

//    protected Claims claims;


    /**
     * 进入控制器方法之前执行的内容
     * 使用jwt方式获取
     * @param request
     * @param response
     */
   /* @ModelAttribute
    public void setResAndReq(HttpServletRequest request,HttpServletResponse response){
        this.request=request;
        this.response=response;
        Object obj = request.getAttribute("user_claims");
        if(obj!=null) {
            this.claims = (Claims) obj;
            this.companyId= (String) this.claims.get("companyId");
            this.companyName= (String) this.claims.get("companyName");
        }

    }*/

    @ModelAttribute
    public void setResAndReq(HttpServletRequest request,HttpServletResponse response){
        this.request=request;
        this.response=response;

        Subject subject = SecurityUtils.getSubject();
        PrincipalCollection principalCollection = subject.getPrincipals();
        if(principalCollection!=null && !principalCollection.isEmpty()){
            ProfileResult profileResult = (ProfileResult) principalCollection.getPrimaryPrincipal();
            this.companyId= profileResult.getCompanyId();
            this.companyName=profileResult.getCompany();
        }
    }


}
