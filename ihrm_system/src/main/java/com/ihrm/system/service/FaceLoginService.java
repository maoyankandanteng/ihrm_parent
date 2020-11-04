package com.ihrm.system.service;


import com.baidu.aip.util.Base64Util;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.utils.IdWorker;
import com.ihrm.domain.system.User;
import com.ihrm.domain.system.response.FaceLoginResult;
import com.ihrm.domain.system.response.QRCode;
import com.ihrm.system.utils.BaiduAiUtil;
import com.ihrm.system.utils.QRCodeUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class FaceLoginService {

    @Value("${qr.url}")
    private String url;
    
    @Autowired
    private IdWorker idWorker;

    @Autowired
    private QRCodeUtil qrCodeUtil;

    @Autowired
    private BaiduAiUtil baiduAiUtil;

    @Autowired UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

	//创建二维码
    public QRCode getQRCode() throws Exception {
        String code=idWorker.nextId()+"";
        String content=url+"?code="+code;
        System.out.println(content);
        String s = qrCodeUtil.crateQRCode(content);

        System.out.println(s);
        FaceLoginResult faceLoginResult=new FaceLoginResult("-1");

        redisTemplate.boundValueOps(getCacheKey(code)).set(faceLoginResult,10, TimeUnit.MINUTES);
        return new QRCode(code,s);
    }

	//根据唯一标识，查询用户是否登录成功
    public FaceLoginResult checkQRCode(String code) {
        String key =getCacheKey(code);
        return (FaceLoginResult) redisTemplate.opsForValue().get(key);
    }

	//扫描二维码之后，使用拍摄照片进行登录
    public String loginByFace(String code, MultipartFile attachment) throws Exception {
        String image = Base64Util.encode(attachment.getBytes());
        String userId = baiduAiUtil.faceSearch(image);
        FaceLoginResult faceLoginResult = new FaceLoginResult("0", null, null);
        if(userId !=null){
            User user = userService.findById(userId);
            if(user !=null){
                UsernamePasswordToken token=new UsernamePasswordToken(user.getMobile(),user.getPassword());
                Subject subject = SecurityUtils.getSubject();
                subject.login(token);
                String sessionId = subject.getSession().getId().toString();
                faceLoginResult=new FaceLoginResult("1",sessionId,userId);
            }
        }
        redisTemplate.boundValueOps(getCacheKey(code)).set(faceLoginResult,10,TimeUnit.MINUTES);
        return userId;
    }

	//构造缓存key
    private String getCacheKey(String code) {
        return "qrcode_" + code;
    }
}
