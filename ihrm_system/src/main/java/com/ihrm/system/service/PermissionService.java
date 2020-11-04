package com.ihrm.system.service;

import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.exception.CommonException;
import com.ihrm.common.service.BaseService;
import com.ihrm.common.utils.BeanMapUtils;
import com.ihrm.common.utils.IdWorker;
import com.ihrm.common.utils.PermissionConstants;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.PermissionApi;
import com.ihrm.domain.system.PermissionMenu;
import com.ihrm.domain.system.PermissionPoint;
import com.ihrm.system.dao.PermissionApiDao;
import com.ihrm.system.dao.PermissionDao;
import com.ihrm.system.dao.PermissionMenuDao;
import com.ihrm.system.dao.PermissionPointDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class PermissionService extends BaseService<Permission> {
    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private PermissionMenuDao permissionMenuDao;

    @Autowired
    private PermissionPointDao permissionPointDao;

    @Autowired
    private PermissionApiDao permissionApiDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 更新权限
     * @param map
     */
    public void update(Map<String,Object> map) throws Exception {
        Permission permission = BeanMapUtils.mapToBean(map, Permission.class);
        Permission permissionById= permissionDao.findById(permission.getId()).get();
        permissionById.setName(permission.getName());
        permissionById.setDescription(permission.getDescription());
        permissionById.setCode(permission.getCode());
        permissionById.setEnVisible(permission.getEnVisible());
        int type=permission.getType();
        switch (type){
            case PermissionConstants.PY_MENU:
                PermissionMenu permissionMenu = BeanMapUtils.mapToBean(map, PermissionMenu.class);
                permissionMenuDao.save(permissionMenu);
                break;
            case PermissionConstants.PY_POINT:
                PermissionPoint permissionPoint = BeanMapUtils.mapToBean(map, PermissionPoint.class);
                permissionPointDao.save(permissionPoint);
                break;
            case PermissionConstants.PY_API:
                PermissionApi permissionApi = BeanMapUtils.mapToBean(map, PermissionApi.class);
                permissionApiDao.save(permissionApi);
                break;
            default:throw new CommonException(ResultCode.FAIL);
        }
        permissionDao.save(permission);
    }

    /**
     * 保存权限
     * @param map
     */
    public void save(Map<String,Object> map) throws Exception {
        //基本属性的设置
        String id=idWorker.nextId()+"";
        Permission permission = BeanMapUtils.mapToBean(map, Permission.class);
        permission.setId(id);
        int type=permission.getType();
        switch (type){
            case PermissionConstants.PY_MENU:
                PermissionMenu permissionMenu = BeanMapUtils.mapToBean(map, PermissionMenu.class);
                permissionMenu.setId(id);
                permissionMenuDao.save(permissionMenu);
                break;
            case PermissionConstants.PY_POINT:
                PermissionPoint permissionPoint = BeanMapUtils.mapToBean(map, PermissionPoint.class);
                permissionPoint.setId(id);
                permissionPointDao.save(permissionPoint);
                break;
            case PermissionConstants.PY_API:
                PermissionApi permissionApi = BeanMapUtils.mapToBean(map, PermissionApi.class);
                permissionApi.setId(id);
                permissionApiDao.save(permissionApi);
                break;
            default:throw new CommonException(ResultCode.FAIL);
        }
        permissionDao.save(permission);
    }

    /**
     * 根据ID 删除权限
     * @param id
     */
    public void deleteById(String id) throws CommonException {
        Permission permissionById= permissionDao.findById(id).get();
        permissionDao.deleteById(id);
        int type=permissionById.getType();
        switch (type){
            case PermissionConstants.PY_MENU:
                permissionMenuDao.deleteById(id);
                break;
            case PermissionConstants.PY_POINT:
                permissionPointDao.deleteById(id);
                break;
            case PermissionConstants.PY_API:
                permissionApiDao.deleteById(id);
                break;
            default:  throw  new CommonException(ResultCode.FAIL);
        }
    }

    public Map<String,Object> findById(String id) throws CommonException {
        Permission permission = permissionDao.findById(id).get();
        int type=permission.getType();
        Object object=null;
        if(type==PermissionConstants.PY_MENU){
            object=permissionMenuDao.findById(id).get();
        }else if(type==PermissionConstants.PY_POINT){
            object=permissionPointDao.findById(id).get();
        }else if(type==PermissionConstants.PY_API){
            object=permissionApiDao.findById(id).get();
        }else {
            throw new CommonException(ResultCode.FAIL);
        }
        Map<String, Object> map = BeanMapUtils.beanToMap(object);
        map.put("name",permission.getName());
        map.put("type",permission.getType());
        map.put("code",permission.getCode());
        map.put("description",permission.getDescription());
        map.put("pid",permission.getPid());
        map.put("enVisible",permission.getEnVisible());
        return map;
    }

    /**
     *
     * @param map
     * @return
     */
    public List<Permission> findSearch(Map<String, Object> map) {
        Specification<Permission> specification=new Specification<Permission>() {
            @Override
            public Predicate toPredicate(Root<Permission> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list=new ArrayList<>();
                //根据父id查询
                if(!StringUtils.isEmpty(map.get("pid"))){
                    list.add(criteriaBuilder.equal(root.get("pid").as(String.class),map.get("pid")));
                }
                //根据enVisible 0: 查询全部  1 查询企业自己的权限
                if(!StringUtils.isEmpty(map.get("enVisible"))){
                    list.add(criteriaBuilder.equal(root.get("enVisible").as(String.class),map.get("enVisible")));
                }
                CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("type"));
                //根据type 0 :菜单+按钮  1 菜单 2 按钮 3 api
                if(!StringUtils.isEmpty(map.get("type"))){
                    String type= (String) map.get("type");
                    if("0".equals(type)){
                        in.value(1).value(2);
                    }else{
                        in.value(Integer.parseInt(type));
                    }
                    list.add(in);
                }
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return permissionDao.findAll(specification);
    }
}
