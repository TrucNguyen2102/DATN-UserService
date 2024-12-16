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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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

    // Lưu người dùng vào cơ sở dữ liệu
    public User save(User user) {
        return userRepo.save(user);
    }

    //lưu tk của khách vãng lai (mới)
    public User createTemporaryUser(String fullName, String phone) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đầy đủ không được để trống.");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống.");
        }

        // Tìm Role "GUEST"
        Role guestRole = roleRepo.findByName("GUEST");

        if (guestRole == null) {
            throw new IllegalStateException("Role 'GUEST' không tồn tại.");
        }

        // Tạo User
        User newUser = new User();
        newUser.setFullName(fullName);
        newUser.setPhone(phone);
        newUser.setBirthDay(null);
        newUser.setEmail("");  // Không cần thiết cho khách vãng lai
        newUser.setPassword("");  // Không cần thiết cho khách vãng lai
        newUser.setStatus(UserStatus.GUEST);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());


        // Lưu User vào bảng User
        newUser = userRepo.save(newUser);

        // Tạo mối quan hệ giữa User và Role 'GUEST' trong bảng UserRole
        Role_User userRole = new Role_User();
        RoleUserId roleUserId = new RoleUserId(newUser.getId(), guestRole.getId()); // Tạo composite key
        userRole.setId(roleUserId); // Gán composite key
        userRole.setUser(newUser); // Gán user
        userRole.setRole(guestRole); // Gán role

        // Lưu mối quan hệ vào bảng UserRole
        roleUserRepo.save(userRole);

        return newUser;
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

    public String getEmailByUserId(Integer userId) {
        User user = userRepo.findById(userId).orElse(null);
        return (user != null) ? user.getEmail() : null;
    }

    @Override
    public void addStaff(StaffDTO staffDTO) {
        User user = new User();
        user.setFullName(staffDTO.getFullName());
        user.setBirthDay(staffDTO.getBirthDay());
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
        // Kiểm tra số điện thoại đã tồn tại
        if (userRepo.existsByPhone(managerDTO.getPhone())) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại: " + managerDTO.getPhone());
        }

        // Kiểm tra email đã tồn tại
//        if (userRepo.existsByEmail(managerDTO.getEmail())) {
//            throw new IllegalArgumentException("Email đã tồn tại: " + managerDTO.getEmail());
//        }

        User user = new User();
        user.setFullName(managerDTO.getFullName());
        user.setBirthDay(managerDTO.getBirthDay());
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

//    // Lấy tất cả người dùng với các role của họ
//    public List<UserDTO> getAllUsersWithRoles() {
//        List<User> users = userRepo.findAll();
//
//        // Chuyển từ User entity sang UserDTO
//        return users.stream().map(user -> {
//            Set<String> roleNames = user.getRoles().stream()
//                    .map(roleUser -> roleUser.getRole().getName())
//                    .collect(Collectors.toSet());
//            return new UserDTO(user.getId(), user.getFullName(), user.getBirthDay(), user.getPhone(), user.getEmail(), user.getStatus(), roleNames, user.getCreatedAt(), user.getUpdatedAt());
//        }).collect(Collectors.toList());
//    }

    public Page<UserDTO> getAllUsersWithRoles(Pageable pageable) {
        Page<User> users = userRepo.findAll(pageable);

        // Chuyển từ User entity sang UserDTO
        return users.map(user -> {
            Set<String> roleNames = user.getRoles().stream()
                    .map(roleUser -> roleUser.getRole().getName())
                    .collect(Collectors.toSet());
            return new UserDTO(user.getId(), user.getFullName(), user.getBirthDay(), user.getPhone(), user.getEmail(), user.getStatus(), roleNames, user.getCreatedAt(), user.getUpdatedAt());
        });
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


//    public List<User> searchUsers(String fullName, String phone) {
//        List<User> users = userRepo.findUserByFullNameOrPhone(fullName, phone);
//        if (users.isEmpty()) {
//            System.out.println("Không tìm thấy người dùng với tên: " + fullName + " và phone: " + phone);
//        }
//
//        return users;
//    }

    public List<User> searchUsers(String fullName, String phone) {
        if (fullName != null && !fullName.isEmpty() && phone != null && !phone.isEmpty()) {
            // Tìm kiếm khi cả tên và số điện thoại đều có giá trị
            return userRepo.findByFullNameContainingOrPhoneContaining(fullName, phone);
        } else if (fullName != null && !fullName.isEmpty()) {
            // Tìm kiếm khi chỉ có tên
            return userRepo.findByFullNameContaining(fullName);
        } else if (phone != null && !phone.isEmpty()) {
            // Tìm kiếm khi chỉ có số điện thoại
            return userRepo.findByPhoneContaining(phone);
        }

        // Nếu không có thông tin tìm kiếm nào thì trả về danh sách trống hoặc tất cả người dùng
        return new ArrayList<>();
    }




//    public boolean changePassword(String oldPassword, String newPassword) {
//        // Giả sử bạn có phương thức để lấy thông tin người dùng từ database
//        User user = getCurrentUser(); // Bạn cần phương thức để lấy người dùng hiện tại (ví dụ: từ session hoặc JWT token)
//
//        // Kiểm tra mật khẩu cũ
//        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
//            // Nếu mật khẩu cũ đúng, mã hóa mật khẩu mới
//            String encodedNewPassword = passwordEncoder.encode(newPassword);
//            user.setPassword(encodedNewPassword);
//
//            // Lưu mật khẩu mới vào cơ sở dữ liệu
//            userRepository.save(user);
//            return true;
//        }
//        // Trả về false nếu mật khẩu cũ không đúng
//        return false;
//    }

    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

    public User updateUpdatedAt(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại với ID: " + userId));
        user.setUpdatedAt(LocalDateTime.now());
        return userRepo.save(user);
    }

    public Page<UserDTO> getUsersByRoleName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepo.findUsersByRoleName(name, pageable);

        // Chuyển đổi từ User sang UserDTO
        Page<UserDTO> userDTOs = usersPage.map(user -> UserDTO.fromUser(user)); // Sử dụng phương thức fromUser để chuyển đổi
        return userDTOs;
    }

//    public List<User> getActiveUsers() {
//        return userRepo.findByStatus(UserStatus.ACTIVE);
//    }

//    @Transactional
//    public User updateManager(Integer id, ManagerDTO managerDTO) throws Exception {
//        // Tìm người quản lý theo id
//        Optional<User> existingManager = userRepo.findById(id);
//
//        if (existingManager.isEmpty()) {
//            throw new Exception("Quản lý không tồn tại.");
//        }
//
//        User manager = existingManager.get();
//
//        // Nếu có gửi đầy đủ thông tin (cho việc thêm mới), cập nhật chúng
//        if (managerDTO.getFullName() != null) {
//            manager.setFullName(managerDTO.getFullName());
//        }
//        if (managerDTO.getBirthDay() != null) {
//            manager.setBirthDay(managerDTO.getBirthDay());
//        }
//        if (managerDTO.getEmail() != null) {
//            manager.setEmail(managerDTO.getEmail());
//        }
//        if (managerDTO.getPhone() != null) {
//            manager.setPhone(managerDTO.getPhone());
//        }
//
//        // Cập nhật vai trò nếu có
//        if (managerDTO.getRole() != null) {
//            // Lấy vai trò cũ (nếu có) của người quản lý
//            Set<Role_User> currentRoles = roleUserRepo.findAllByUserId(manager.getId());
//
//            // Kiểm tra nếu người quản lý có vai trò
//            if (currentRoles != null && !currentRoles.isEmpty()) {
//                // Lấy vai trò mới từ cơ sở dữ liệu
//                Role newRole = roleRepo.findByName(managerDTO.getRole());
//                if (newRole == null) {
//                    throw new Exception("Vai trò không tồn tại.");
//                }
//
//                // Cập nhật vai trò cũ thành vai trò mới
//                for (Role_User roleUser : currentRoles) {
//                    roleUser.setRole(newRole);  // Cập nhật vai trò
//                    roleUserRepo.save(roleUser);  // Lưu cập nhật
//                }
//            } else {
//                // Nếu không có vai trò cũ, tạo mới vai trò cho người quản lý
//                Role newRole = roleRepo.findByName(managerDTO.getRole());
//                if (newRole == null) {
//                    throw new Exception("Vai trò không tồn tại.");
//                }
//
//                // Gán vai trò mới cho người quản lý
//                Role_User newRoleUser = new Role_User(newRole, manager);
//                roleUserRepo.save(newRoleUser);
//            }
//        }
//
//        // Cập nhật trạng thái nếu có (Nếu không có, vẫn giữ nguyên trạng thái cũ)
//        if (managerDTO.getStatus() != null) {
//            manager.setStatus(UserStatus.valueOf(managerDTO.getStatus()));
//        }
//
//        // Lưu lại người quản lý đã cập nhật
//        return userRepo.save(manager);
//    }



    @Transactional
    public User updateManager(Integer id, ManagerDTO managerDTO) throws Exception {
        // Tìm người quản lý theo id
        Optional<User> existingManager = userRepo.findById(id);

        if (existingManager.isEmpty()) {
            throw new Exception("Quản lý không tồn tại.");
        }

        User manager = existingManager.get();

        // Nếu có gửi đầy đủ thông tin (cho việc thêm mới), cập nhật chúng
        if (managerDTO.getFullName() != null) {
            manager.setFullName(managerDTO.getFullName());
        }
        if (managerDTO.getBirthDay() != null) {
            manager.setBirthDay(managerDTO.getBirthDay());
        }
        if (managerDTO.getEmail() != null) {
            manager.setEmail(managerDTO.getEmail());
        }
        if (managerDTO.getPhone() != null) {
            manager.setPhone(managerDTO.getPhone());
        }

        // Cập nhật vai trò nếu có
        if (managerDTO.getRole() != null) {
            // Lấy tất cả các vai trò hiện tại của người quản lý theo userId
            Set<Role_User> currentRoles = roleUserRepo.findAllByUserId(manager.getId());

            // Kiểm tra và xóa vai trò cũ
            if (currentRoles != null && !currentRoles.isEmpty()) {
                // Xóa các vai trò cũ theo cách thủ công, thay vì sử dụng deleteAll
                for (Role_User roleUser : currentRoles) {
                    roleUserRepo.delete(roleUser); // Xóa từng vai trò cũ
                }
            }

            // Tạo vai trò mới
            Role newRole = roleRepo.findByName(managerDTO.getRole());
            if (newRole == null) {
                throw new Exception("Vai trò không tồn tại.");
            }

            // Tạo và lưu vai trò mới cho người quản lý
            Role_User newRoleUser = new Role_User(newRole, manager);
            roleUserRepo.save(newRoleUser); // Lưu vai trò mới


        }

        // Cập nhật trạng thái nếu có
        if (managerDTO.getStatus() != null) {
            manager.setStatus(UserStatus.valueOf(managerDTO.getStatus()));
        }

        // Lưu lại người quản lý đã cập nhật
        return userRepo.save(manager);
    }









}
