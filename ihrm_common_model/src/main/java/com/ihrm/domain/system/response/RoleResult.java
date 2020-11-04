package com.ihrm.domain.system.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.Role;
import com.ihrm.domain.system.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class RoleResult implements Serializable {
    @Id
    private String id;
    /**
     * 角色名
     */
    private String name;
    /**
     * 说明
     */
    private String description;
    /**
     * 企业id
     */
    private String companyId;

    private List<String> permIds=new ArrayList<>();
    public RoleResult(Role role){
        BeanUtils.copyProperties(role,this);
        for (Permission permission:role.getPermissions()){
            this.permIds.add(permission.getId());
        }
    }
}
