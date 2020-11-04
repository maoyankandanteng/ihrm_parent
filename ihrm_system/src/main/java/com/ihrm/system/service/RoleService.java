package com.ihrm.system.service;

import com.ihrm.common.service.BaseService;
import com.ihrm.common.utils.IdWorker;
import com.ihrm.common.utils.PermissionConstants;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.Role;
import com.ihrm.system.dao.PermissionDao;
import com.ihrm.system.dao.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService extends BaseService<Role> {
    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 保存用户
     *
     * @param role
     */
    public void save(Role role) {
        //基本属性的设置
        String id = idWorker.nextId() + "";
        role.setId(id);
        roleDao.save(role);
    }

    /**
     * 更新用户
     *
     * @param role
     */
    public void update(Role role) {
        Role target = roleDao.findById(role.getId()).get();
        target.setDescription(role.getDescription());
        target.setName(role.getName());
        roleDao.save(target);
    }

    public void deleteById(String id) {
        roleDao.deleteById(id);
    }

    public Role findById(String id) {
        return roleDao.findById(id).get();
    }

    /**
     * @param companyId
     * @param page
     * @param size
     * @return
     */
    public Page<Role> findSearch(String companyId, int page, int size) {
        Specification<Role> specification = new Specification<Role>() {
            @Override
            public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("companyId").as(String.class), companyId);
            }
        };
        return roleDao.findAll(specification, PageRequest.of(page - 1, size));
    }

    /**
     * @param companyId
     * @return
     */
    public List<Role> findAll(String companyId) {
        Specification<Role> specification = new Specification<Role>() {
            @Override
            public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("companyId").as(String.class), companyId);
            }
        };
        return roleDao.findAll(specification);
    }

    /**
     * 分配角色
     *
     * @param roleId
     * @param permIds
     */
    public void assignPerm(String roleId, List<String> permIds) {
        Role role = roleDao.findById(roleId).get();
        Set<Permission> permissions = new HashSet<>();
        for (String permId : permIds) {
            Permission permission = permissionDao.findById(permId).get();
            List<Permission> apiList = permissionDao.findByTypeAndPid(PermissionConstants.PY_API, permission.getId());
            permissions.addAll(apiList);//api权限
            permissions.add(permission);//菜单和按钮权限
        }
        role.setPermissions(permissions);
        roleDao.save(role);
    }
}
