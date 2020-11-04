package com.ihrm.company.service;

import com.ihrm.common.service.BaseService;
import com.ihrm.common.utils.IdWorker;
import com.ihrm.company.dao.DepartmentDao;
import com.ihrm.domain.company.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
public class DepartmentService extends BaseService<Department> {
    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 保存部门
     * @param department
     */
    public void save(Department department) {
        //基本属性的设置
        String id=idWorker.nextId()+"";
        department.setId(id);
        departmentDao.save(department);
    }

    /**
     * 更新部门
     * @param department
     */
    public void update(Department department) {
        Department dept = departmentDao.findById(department.getId()).get();
        dept.setCode(department.getCode());
        dept.setIntroduce(department.getIntroduce());
        dept.setName(department.getName());
        departmentDao.save(dept);
    }

    public void deleteById(String id) {
        departmentDao.deleteById(id);
    }

    public Department findById(String id) {
        return departmentDao.findById(id).get();
    }

    public List<Department> findAll(String companyId) {
//        Specification<Department> specification=new Specification<Department>() {
//            /**
//             * 构造查询条件
//             * @param root
//             * @param criteriaQuery
//             * @param criteriaBuilder
//             * @return
//             */
//            @Override
//            public Predicate toPredicate(Root<Department> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
//                return criteriaBuilder.equal(root.get("companyId").as(String.class),companyId);
//            }
//        };
        return departmentDao.findAll(getSpec(companyId));
    }

    public Department findByCode(String code, String companyId) {
        return departmentDao.findByCodeAndCompanyId(code,companyId);
    }
}
