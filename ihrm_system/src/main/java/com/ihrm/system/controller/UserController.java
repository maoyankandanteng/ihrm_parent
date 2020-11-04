package com.ihrm.system.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.exception.CommonException;
import com.ihrm.common.poi.ExcelImportUtil;
import com.ihrm.common.utils.JwtUtils;
import com.ihrm.common.utils.PermissionConstants;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.Role;
import com.ihrm.domain.system.User;
import com.ihrm.domain.system.response.ProfileResult;
import com.ihrm.domain.system.response.UserResult;
import com.ihrm.system.client.DepartmentFeignClient;
import com.ihrm.system.service.PermissionService;
import com.ihrm.system.service.UserService;
import io.jsonwebtoken.Claims;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//1.解决跨域
@CrossOrigin
//2.声明restContoller
@RestController
//3.设置父路径
@RequestMapping(value = "/system")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    //JWT json web token
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private DepartmentFeignClient departmentFeignClient;

    //上传头像
    @RequestMapping(value = "/user/upload/{id}", method = RequestMethod.POST)
    public Result upload(@PathVariable("id") String id,@RequestParam("file")MultipartFile file) throws Exception {

       String imgUrl=userService.uploadImage(id,file);
        return new Result(ResultCode.SUCCESS,imgUrl);
    }

    //保存用户
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public Result add(@RequestBody User user) throws Exception {
        user.setCompanyId(companyId);
        user.setCompanyName(companyName);
        userService.save(user);
        return Result.SUCCESS();
    }

    //更新用户
    @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
    public Result update(@PathVariable(name = "id") String id, @RequestBody User user)
            throws Exception {
        userService.update(user);
        return Result.SUCCESS();
    }

    //删除用户
    @RequiresPermissions(value = "API-USER-DELETE")
    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE,name = "API-USER-DELETE")
    public Result delete(@PathVariable(name = "id") String id) throws Exception {
        userService.deleteById(id);
        return Result.SUCCESS();
    }

    /**
     *     * 根据ID查询用户
     *    
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable(name = "id") String id) throws Exception {
        User user = userService.findById(id);
        UserResult userResult = new UserResult(user);
        return new Result(ResultCode.SUCCESS, userResult);
    }

    /**
     *     * 分页查询用户
     *    
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Result findByPage(int page, int size, @RequestParam Map<String, Object>
            map) throws Exception {
        map.put("companyId", companyId);
        Page<User> searchPage = userService.findSearch(map, page, size);
        PageResult<User> pr = new
                PageResult(searchPage.getTotalElements(), searchPage.getContent());
        return new Result(ResultCode.SUCCESS, pr);
    }

    /**
     * @param map {
     *            "id":用户ID
     *            "roleIds":[]
     *            }
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/assignRoles", method = RequestMethod.PUT)
    public Result assignRoles(@RequestBody Map<String, Object>
                                      map) throws Exception {
        String userId = (String) map.get("id");
        List<String> roleIds = (List<String>) map.get("roleIds");
        userService.assignRoles(userId, roleIds);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 登陆
     *
     * @param loginMap
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result login(@RequestBody Map<String, String> loginMap) throws Exception {
        String mobile = loginMap.get("mobile");
        String password = loginMap.get("password");

        try {
            String passwordMd5 = new Md5Hash(password, mobile, 3).toString();
            UsernamePasswordToken token=new UsernamePasswordToken(mobile,passwordMd5);
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);
            String sessionId = subject.getSession().getId().toString();
            return new Result(ResultCode.SUCCESS, sessionId);
        }catch (Exception e){
            return new Result(ResultCode.MOBILEORPASSWORDERROR);
        }
      /*  User user = userService.findByMobile(mobile);
        if (user == null || !user.getPassword().equals(password)) {
            return new Result(ResultCode.MOBILEORPASSWORDERROR);
        } else {
            //登陆成功
            //获取到所有的可访问的API权限
            StringBuilder sb=new StringBuilder();
            for(Role role:user.getRoles()){
                for(Permission perm : role.getPermissions()){
                    if(perm.getType()== PermissionConstants.PY_API){
                        sb.append(perm.getCode()).append(",");
                    }
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("name", user.getUsername());
            map.put("companyId", user.getCompanyId());
            map.put("companyName", user.getCompanyName());
            map.put("apis",sb.toString());
            String token = jwtUtils.createJwt(map);
            return new Result(ResultCode.SUCCESS, token);
        }*/
    }

    /**
     * 登陆成功之后获取用户信息
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public Result profile(HttpServletRequest request) throws Exception {
        //获取到session的安全数据
        Subject subject = SecurityUtils.getSubject();
        PrincipalCollection principalCollection = subject.getPrincipals();
        ProfileResult  profileResult = (ProfileResult) principalCollection.getPrimaryPrincipal();

        /*String userId = (String) claims.get("id");
        User user = userService.findById(userId);
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
        }*/
        return new Result(ResultCode.SUCCESS, profileResult);
    }

    /*
    public static void main(String[] args) {
        String passwordMd5 = new Md5Hash("123456", "2019046150", 3).toString();
        System.out.println(passwordMd5);
    }*/

    /**
     * 测试Feign组件
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/test/{id}", method = RequestMethod.GET)
    public Result test(@PathVariable(name = "id") String id) throws Exception {
        Result result = departmentFeignClient.findById(id);
        return result;
    }


    /**
     * 测试Feign组件
     * @return
     * */
    @RequestMapping(value = "/user/import", method = RequestMethod.POST)
    public Result test(@RequestParam(value = "file")MultipartFile file) throws Exception {
      /*  //1.创建工作簿 HSSFWorkBook 03版本的
        Workbook wb=new XSSFWorkbook(file.getInputStream());
        //2.创建表单sheet
        Sheet sheet = wb.getSheetAt(0);
        List<User> list=new ArrayList<>();
        for(int rowNum=1;rowNum<=sheet.getLastRowNum();rowNum++){
            Row row = sheet.getRow(rowNum);
            Object[] vlaues=new Object[row.getLastCellNum()];
            for(int cellNum=1;cellNum<row.getLastCellNum();cellNum++){
                Cell cell = row.getCell(cellNum);
                Object value=getCellValue(cell);
                vlaues[cellNum]=value;
            }
            User user=new User(vlaues);
            list.add(user);
        }*/
        List<User> users = new ExcelImportUtil<User>(User.class).readExcel(file.getInputStream(), 1, 1);
        userService.saveAll(users,companyId,companyName);
        return new Result(ResultCode.SUCCESS);
    }

    private  Object getCellValue(Cell cell){
        CellType cellType = cell.getCellType();
        Object value=null;
        switch (cellType){
            case STRING:
                value=cell.getStringCellValue();
                break;
            case BOOLEAN:
                value=cell.getBooleanCellValue();
                break;
            case NUMERIC:
                if(DateUtil.isCellDateFormatted(cell)){
                    value=cell.getDateCellValue();
                }else{
                    value=cell.getNumericCellValue();
                }
                break;
            case FORMULA:
                value=cell.getCellFormula();
                break;
            default:break;
        }
        return value;
    }

}
