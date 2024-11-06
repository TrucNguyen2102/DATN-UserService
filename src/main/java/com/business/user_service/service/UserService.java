package com.business.user_service.service;

import com.business.user_service.dto.ManagerDTO;
import com.business.user_service.dto.StaffDTO;
import com.business.user_service.dto.UserDTO;
import com.business.user_service.entity.Role_User;
import com.business.user_service.entity.User;

import java.util.List;

public interface UserService {
    User findByPhone(String phone);

    void saveUser(User user);


    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);


    List<User> getAllUsers();

    User findByFullName(String fullName);

    void updateUser(User user);

    User findById(Integer id);

    void addStaff(StaffDTO staffDTO);

    void addManager(ManagerDTO managerDTO);

//    void editUser(Integer id, StaffDTO staffDTO);

    void lockUser(Integer id);

    void unlockUser(Integer userId);

    void saveRoleUser(Role_User roleUser);

    List<UserDTO> getAllUsersWithRoles();



    List<User> getCustomers();

    List<UserDTO> getUsersByRole(String roleName);


}
