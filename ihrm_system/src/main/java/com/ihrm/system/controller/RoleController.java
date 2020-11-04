package com.ihrm.system.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.domain.system.Role;
import com.ihrm.domain.system.response.RoleResult;
import com.ihrm.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    @RequestMapping(value = "/role", method = RequestMethod.POST)
    public Result add(@RequestBody Role role) throws Exception {
        String companyId = "1";
        role.setCompanyId(companyId);
        roleService.save(role);
        return Result.SUCCESS();
    }

    //更新角色
    @RequestMapping(value = "/role/{id}", method = RequestMethod.PUT)
    public Result update(@PathVariable(name = "id") String id, @RequestBody Role role)
            throws Exception {
        roleService.update(role);
        return Result.SUCCESS();
    }

    //删除角色
    @RequestMapping(value = "/role/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable(name = "id") String id) throws Exception {
        roleService.deleteById(id);
        return Result.SUCCESS();
    }

    /**
     *     * 根据ID获取角色信息
     *    
     */
    @RequestMapping(value = "/role/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable(name = "id") String id) throws Exception {

        Role role = roleService.findById(id);
        RoleResult roleResult = new RoleResult(role);
        return new Result(ResultCode.SUCCESS, roleResult);
    }

    /**
     *     * 分页查询角色
     *    
     */

    @RequestMapping(value = "/role", method = RequestMethod.GET)
    public Result findByPage(int page, int pagesize, Role role) throws Exception {
        String companyId = "1";
        Page<Role> searchPage = roleService.findSearch(companyId, page, pagesize);
        PageResult<Role> pr = new
                PageResult(searchPage.getTotalElements(), searchPage.getContent());
        return new Result(ResultCode.SUCCESS, pr);
    }

    /**
     * 查找公司所有权限
     *    
     */

    @RequestMapping(value = "/role/list", method = RequestMethod.GET)
    public Result findAll() throws Exception {
        String companyId = "1";
        List<Role> all = roleService.findAll(companyId);
        return new Result(ResultCode.SUCCESS, all);
    }

    /**
     * @param map {
     *            "id":用户ID
     *            "roleIds":[]
     *            }
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/role/assignPerm", method = RequestMethod.PUT)
    public Result assignPerm(@RequestBody Map<String, Object>
                                      map) throws Exception {
        String roleId = (String) map.get("id");
        List<String> permIds= (List<String>) map.get("permIds");
        roleService.assignPerm(roleId,permIds);
        return new Result(ResultCode.SUCCESS);
    }
}
