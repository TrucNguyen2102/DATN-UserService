package com.business.user_service.service;

import com.business.user_service.dto.ManagerDTO;
import com.business.user_service.dto.RegisterRequest;
import com.business.user_service.dto.StaffDTO;
import com.business.user_service.dto.UserDTO;
import com.business.user_service.entity.*;
import com.business.user_service.repository.RoleRepo;
import com.business.user_service.repository.RoleUserRepo;
import com.business.user_service.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleUserRepo roleUserRepo;
//    @Autowired
//    private AuthorityRepo authorityRepo;
//    @Autowired
//    private AuthorityService authorityService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }



    @Override
    // Phương thức tìm kiếm người dùng theo số điện thoại
    public User findByPhone(String phone) {
        return userRepo.findByPhone(phone);
//                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại với số điện thoại: " + phone));
    }

    //lưu user
    @Override
    public void saveUser(User user) {
        userRepo.save(user);
    }


    @Override
    public boolean existsByPhone(String phone) {
        return userRepo.existsByPhone(phone);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User findByFullName(String fullName) {
        return userRepo.findByFullName(fullName);
    }

    @Override
    public void updateUser(User user) {
        userRepo.save(user);
    }

    @Override
    public User findById(Integer id) {
        Optional<User> userOptional = userRepo.findById(id);
        return userOptional.orElse(null); // Trả về user nếu tìm thấy, nếu không trả về null
    }

    @Override
    public void addStaff(StaffDTO staffDTO) {
        User user = new User();
        user.setFullName(staffDTO.getFullName());
        user.setPhone(staffDTO.getPhone());
        user.setEmail(staffDTO.getEmail());

        // Mã hóa mật khẩu
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(staffDTO.getPassword());
        user.setPassword(encodedPassword);


        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepo.save(user);

        // Lấy đối tượng Role từ tên vai trò
        Role role = roleRepo.findByName(staffDTO.getRole());

        if (role == null) {
            throw new IllegalArgumentException("Vai trò không hợp lệ: " + staffDTO.getRole());
        }

        // Lưu vào bảng Role_User
        Role_User roleUser = new Role_User(role, user);
        roleUserRepo.save(roleUser);
    }

    @Override
    public void addManager(ManagerDTO managerDTO) {
        User user = new User();
        user.setFullName(managerDTO.getFullName());
        user.setPhone(managerDTO.getPhone());
        user.setEmail(managerDTO.getEmail());

        // Mã hóa mật khẩu
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(managerDTO.getPassword());
        user.setPassword(encodedPassword);

        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Lưu người dùng
        userRepo.save(user);

        // Lấy đối tượng Role từ tên vai trò
        Role role = roleRepo.findByName(managerDTO.getRole());

        if (role == null) {
            throw new IllegalArgumentException("Vai trò không hợp lệ: " + managerDTO.getRole());
        }

        // Lưu vào bảng Role_User
        Role_User roleUser = new Role_User(role, user);
        roleUserRepo.save(roleUser);
    }






//    @Override
//    public void editUser(Integer id, StaffDTO staffDTO) {
//        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên."));
//        user.setFullName(staffDTO.getFullName());
//        user.setEmail(staffDTO.getEmail());
//        user.setPhone(staffDTO.getPhone());
//        user.setPassword(staffDTO.getPassword());
//        // Lấy đối tượng Authority từ tên quyền
//        Authority authority = authorityRepo.findByName(staffDTO.getRole());
//        //user.setAuthority(authority);
//        userRepo.save(user);
//    }

    @Override
    public void lockUser(Integer id) {
//        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên."));
////        user.setStatus("Đã khóa"); // Thay đổi trạng thái thành "Đã khóa"
//        user.setStatus(UserStatus.BLOCKED);
//        userRepo.save(user);
        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setStatus(UserStatus.BLOCKED);
            userRepo.save(user); // Lưu thay đổi vào database
        } else {
            throw new RuntimeException("Không tìm thấy người dùng");
        }
    }

    @Override
    public void unlockUser(Integer id) {
        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setStatus(UserStatus.OPENED); // Đặt trạng thái lại là OPENED
            userRepo.save(user); // Lưu thay đổi vào database
        } else {
            throw new RuntimeException("Không tìm thấy người dùng");
        }
    }

    @Override
    public void saveRoleUser(Role_User roleUser) {
        roleUserRepo.save(roleUser); // Lưu Role_User
    }

    // Lấy tất cả người dùng với các role của họ
    public List<UserDTO> getAllUsersWithRoles() {
        List<User> users = userRepo.findAll();

        // Chuyển từ User entity sang UserDTO
        return users.stream().map(user -> {
            Set<String> roleNames = user.getRoles().stream()
                    .map(roleUser -> roleUser.getRole().getName())
                    .collect(Collectors.toSet());
            return new UserDTO(user.getId(), user.getFullName(), user.getPhone(), user.getEmail(), user.getStatus(), roleNames, user.getCreatedAt(), user.getUpdatedAt());
        }).collect(Collectors.toList());
    }



    public List<User> getCustomers() {
        return userRepo.findByRoleName("CUSTOMER");
    }

    public List<UserDTO> getUsersByRole(String roleName) {
        List<User> users = userRepo.findAll(); // Lấy tất cả người dùng
        return users.stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(roleUser -> roleUser.getRole().getName().equals(roleName)))
                .map(UserDTO::fromUser) // Chuyển thành DTO
                .collect(Collectors.toList());
    }


}
