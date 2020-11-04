package com.ihrm.system.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.exception.CommonException;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.User;
import com.ihrm.system.service.PermissionService;
import com.ihrm.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//1.解决跨域
@CrossOrigin
//2.声明restContoller
@RestController
//3.设置父路径
@RequestMapping(value = "/system")
public class PermissionController extends BaseController {

    @Autowired
    private PermissionService permissionService;

    //保存权限
    @RequestMapping(value = "/permission", method = RequestMethod.POST)
    public Result add(@RequestBody Map<String,Object> map) throws Exception {
        permissionService.save(map);
        return Result.SUCCESS();
    }

    //更新权限
    @RequestMapping(value = "/permission/{id}", method = RequestMethod.PUT)
    public Result update(@PathVariable(name = "id") String id,@RequestBody Map<String,Object> map) throws Exception {
        map.put("id",id);
        permissionService.update(map);
        return Result.SUCCESS();
    }

    //删除权限
    @RequestMapping(value = "/permission/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable(name = "id") String id) throws CommonException {
        permissionService.deleteById(id);
        return Result.SUCCESS();
    }

    /**
     * * 根据ID查询权限
     *    
     */
    @RequestMapping(value = "/permission/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable(name = "id") String id) throws Exception {
        Map<String, Object> map = permissionService.findById(id);
        return new Result(ResultCode.SUCCESS,map);
    }

    /**
     ** 查询权限全部
     *    
     */
    @RequestMapping(value = "/permission", method = RequestMethod.GET)
    public Result findAll(@RequestParam Map<String, Object> map) throws Exception {
        List<Permission> list= permissionService.findSearch(map);
        return new Result(ResultCode.SUCCESS,list);
    }
}
