package com.business.user_service.service;

import com.business.user_service.dto.ManagerDTO;
import com.business.user_service.dto.StaffDTO;
import com.business.user_service.dto.UserDTO;
import com.business.user_service.entity.Role_User;
import com.business.user_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    // Lưu người dùng vào cơ sở dữ liệu
    User save(User user);

    //lưu tk của khách vãng lai (mới)
    User createTemporaryUser(String fullName, String phone);

    User findByPhone(String phone);

    void saveUser(User user);


    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);


    List<User> getAllUsers();

    User findByFullName(String fullName);

    void updateUser(User user);

    User findById(Integer id);

    String getEmailByUserId(Integer userId);

    void addStaff(StaffDTO staffDTO);

    void addManager(ManagerDTO managerDTO);

//    void editUser(Integer id, StaffDTO staffDTO);

    void lockUser(Integer id);

    void unlockUser(Integer userId);

    void saveRoleUser(Role_User roleUser);

//    List<UserDTO> getAllUsersWithRoles();

    Page<UserDTO> getAllUsersWithRoles(Pageable pageable);



    List<User> getCustomers();

    List<UserDTO> getUsersByRole(String roleName);

    List<User> searchUsers(String fullName, String phone);


//    boolean changePassword(String oldPassword, String newPassword);
void changePassword(Integer userId, String oldPassword, String newPassword);

    User updateUpdatedAt(Integer id);

    Page<UserDTO> getUsersByRoleName(String roleName, int page, int size);

    User updateManager(Integer id, ManagerDTO managerDTO) throws Exception;

//    User updateUser(Integer id, User updatedUser);

//    List<User> getActiveUsers();
}
