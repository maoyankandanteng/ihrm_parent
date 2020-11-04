package com.ihrm.company.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.company.service.CompanyService;
import com.ihrm.company.service.DepartmentService;
import com.ihrm.domain.company.Company;
import com.ihrm.domain.company.Department;
import com.ihrm.domain.company.response.DeptListResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

//1.解决跨域
@CrossOrigin
//2.声明RestController
@RestController
//3.设置父路径
@RequestMapping(value = "/company/department")
public class DepartmentController extends BaseController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private CompanyService companyService;

    /**
     * 保存
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.POST)
    public Result save(@RequestBody Department department){
        //设置保存的企业ID
        department.setCompanyId(companyId);
        department.setCreateTime(new Date());
        //调用service完成保存业务
        departmentService.save(department);
        //构造返回结果
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 查询企业的部门列表
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.GET)
    public Result findAll(){
        Company company = companyService.findById(companyId);
        System.out.println(companyId);
        List<Department> depts = departmentService.findAll(companyId);
        DeptListResult deptListResult=new DeptListResult(company,depts);
        return new Result(ResultCode.SUCCESS,deptListResult);
    }

    /**
     * 根据ID查询部门
     * @return
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public Result findById(@PathVariable("id")String id){
        Department department = departmentService.findById(id);
        return new Result(ResultCode.SUCCESS,department);
    }

    /**
     * 根据ID更新部门
     * @return
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.PUT)
    public Result update(@PathVariable("id")String id,@RequestBody Department department){
        department.setId(id);
        System.out.println(department);
        departmentService.update(department);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 根据ID删除部门
     * @return
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public Result deleteByID(@PathVariable("id")String id){
        departmentService.deleteById(id);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 根据ID删除部门
     * @return
     */
    @RequestMapping(value = "/search",method = RequestMethod.POST)
    public Department findByCode(@RequestParam("code")String code,@RequestParam("companyId") String companyId){
        return departmentService.findByCode(code,companyId);
    }
}
