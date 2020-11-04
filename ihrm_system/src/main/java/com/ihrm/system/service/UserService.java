package com.ihrm.system.service;

import com.ihrm.common.utils.IdWorker;
import com.ihrm.common.utils.QiniuUploadUtil;
import com.ihrm.domain.company.Department;
import com.ihrm.domain.system.Role;
import com.ihrm.domain.system.User;
import com.ihrm.system.client.DepartmentFeignClient;
import com.ihrm.system.dao.RoleDao;
import com.ihrm.system.dao.UserDao;
import com.ihrm.system.utils.BaiduAiUtil;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private DepartmentFeignClient departmentFeignClient;

    /**
     * 保存用户
     *
     * @param user
     */
    public void save(User user) {
        //基本属性的设置
        String id = idWorker.nextId() + "";
        String password = new Md5Hash("123456", user.getMobile(), 3).toString();

        user.setId(id);
        user.setPassword(password);
        user.setEnableState(1);
        user.setLevel("user");
        userDao.save(user);
    }

    /**
     * 更新用户
     *
     * @param user
     */
    public void update(User user) {
        User target = userDao.findById(user.getId()).get();
        target.setUsername(user.getUsername());
        target.setPassword(user.getPassword());
        target.setDepartmentId(user.getDepartmentId());
        target.setDepartmentName(user.getDepartmentName());
        userDao.save(target);
    }

    public void deleteById(String id) {
        userDao.deleteById(id);
    }

    public User findById(String id) {
        return userDao.findById(id).get();
    }

    /**
     * @param map hasDept
     *            departmentId
     *            companyId
     * @return
     */
    public Page<User> findSearch(Map<String, Object> map, int page, int size) {
        //1.查询条件
        Specification<User> specification = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                if (!StringUtils.isEmpty(map.get("companyId"))) {
                    list.add(criteriaBuilder.equal(root.get("companyId").as(String.class), map.get("companyId")));
                }
                if (!StringUtils.isEmpty(map.get("departmentId"))) {
                    list.add(criteriaBuilder.equal(root.get("departmentId").as(String.class), map.get("departmentId")));
                }
                if (!StringUtils.isEmpty(map.get("hasDept"))) {
                    //hasDept是否分配部门 0：未分配  1：已分配
                    if ("0".equals(map.get("hasDept"))) {
                        list.add(criteriaBuilder.isNull(root.get("departmentId")));
                    } else {
                        list.add(criteriaBuilder.isNotNull(root.get("departmentId")));
                    }
                }

                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        //2.分页
        return userDao.findAll(specification, PageRequest.of(page - 1, size));
    }


    /**
     * 分配角色
     *
     * @param userId
     * @param roleIds
     */
    public void assignRoles(String userId, List<String> roleIds) {
        User user = userDao.findById(userId).get();
        Set<Role> roles = new HashSet<>();
        for (String roleId : roleIds) {
            Role role = roleDao.findById(roleId).get();
            roles.add(role);
        }
        user.setRoles(roles);
        userDao.save(user);
    }

    /**
     * @param mobile
     * @return
     */
    public User findByMobile(String mobile) {
        return userDao.findByMobile(mobile);
    }

    /**
     * 批量保存用户
     *
     * @param list
     * @param companyId
     * @param companyName
     */
    @Transactional
    public void saveAll(List<User> list, String companyId, String companyName) {
        for (User user : list) {
            user.setPassword(new Md5Hash("123456", user.getMobile(), 3).toString());
            user.setId(idWorker.nextId() + "");
            user.setCompanyId(companyId);
            user.setCompanyName(companyName);
            user.setEnableState(1);
            user.setInServiceStatus(1);
            user.setLevel("user");
            Department department = departmentFeignClient.findByCode(user.getDepartmentId(), companyId);
            if (department != null) {
                user.setDepartmentId(department.getId());
                user.setDepartmentName(department.getName());
            }
            userDao.save(user);
        }
    }

    @Autowired
    BaiduAiUtil baiduAiUtil;

    public String uploadImage(String id, MultipartFile file) throws IOException {
    /*    User user = userDao.findById(id).get();
        String data = "data:iamge/png;base64,"+Base64.encode(file.getBytes());
        user.setStaffPhoto(data);
//        System.out.println(data);
        userDao.save(user);*/
        User user = userDao.findById(id).get();
        String imgUrl = new QiniuUploadUtil().upload(user.getId(), file.getBytes());
        user.setStaffPhoto(imgUrl);
        userDao.save(user);

        Boolean isExist = baiduAiUtil.faceExist(id);
        String image = Base64.encode(file.getBytes());
        if(isExist){
            //更新
            baiduAiUtil.faceUpdate(id,image);
        }else{
            //注册
            baiduAiUtil.faceRegister(id,image);
        }
        return imgUrl;
    }
}
