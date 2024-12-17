package com.business.user_service.controller;

import com.business.user_service.dto.*;
import com.business.user_service.entity.*;
import com.business.user_service.jwt.JwtUtil;
import com.business.user_service.repository.RoleRepo;
import com.business.user_service.repository.RoleUserRepo;
import com.business.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;



    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private RoleUserRepo roleUserRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/endpoints")
    public List<Map<String, String>> getEndpoints() {
        return List.of(
                Map.of("service", "user-service", "method", "POST", "url", "/api/users/create-guest"),
                Map.of("service", "user-service", "method", "POST", "url", "/api/users/login"),
                Map.of("service", "user-service", "method", "GET", "url", "/api/users/all"),
                Map.of("service", "user-service", "method", "PUT", "url", "/api/users/{id}/logout"),
                Map.of("service", "user-service", "method", "GET", "url", "/api/users/fullNameUser"),
                Map.of("service", "user-service", "method", "GET", "url", "/api/users/{userId}"),
                Map.of("service", "user-service", "method", "GET", "url", "/api/users/{id}/email"),
                Map.of("service", "user-service", "method", "PUT", "url", "/api/users/update/{id}"),
                Map.of("service", "user-service", "method", "PUT", "url", "/api/users/change-password/{id}"),

                Map.of("service", "user-service", "method", "GET", "url", "/api/users/admins/total-users"),
                Map.of("service", "user-service", "method", "POST", "url", "/api/users/admins/managers/add"),

                Map.of("service", "user-service", "method", "PUT", "url", "/api/users/managers/update/{id}"),
                Map.of("service", "user-service", "method", "GET", "url", "/api/users/managers/customers/all "),
                Map.of("service", "user-service", "method", "GET", "url", "/api/users/managers/staffs/all "),
                Map.of("service", "user-service", "method", "PUT", "url", "/api/users/managers/staffs/lock/{id}"),
                Map.of("service", "user-service", "method", "PUT", "url", "/api/users/managers/staffs/unlock/{id}"),
                Map.of("service", "user-service", "method", "GET", "url", "/api/users/admins/active/count"),
                Map.of("service", "user-service", "method", "GET", "url", "/api/users/admins/in_active/count"),
                Map.of("service", "user-service", "method", "GET", "url", "/api/users/admins/lock/count"),
                Map.of("service", "user-service", "method", "GET", "url", "/api/users/admins/guest/count")





        );
    }

    //tạo tk khách vãng lai (mới)
    @PostMapping("/create-guest")
    public ResponseEntity<Integer> createTemporaryUser(@RequestBody GuestUserRequest request) {
        try {
            User newUser = userService.createTemporaryUser(request.getFullName(), request.getPhone());
            return ResponseEntity.ok(newUser.getId());  // Trả về chỉ ID của User
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }


    //api sd
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest request) {
        try {

            // Tăng số lượng API gọi
//            userServiceApiCallService.incrementApiCallCount();

            // Tìm người dùng theo số điện thoại
//            User user = userService.findByPhone(request.getPhone());
//            if (user == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thông tin đăng nhập không chính xác.");
//            }

            // Tìm người dùng theo số điện thoại
            User user = userService.findByPhone(request.getPhone());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tài khoản của bạn chưa tồn tại trong hệ thống.");
            }


            // Kiểm tra xem người dùng có mật khẩu không
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tài khoản của bạn chưa tồn tại trong hệ thống.");
            }

            // Kiểm tra trạng thái tài khoản
            if (user.getStatus() == UserStatus.BLOCKED) { // Kiểm tra trạng thái
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tài khoản đã bị khóa.");
            }

            // Kiểm tra xem người dùng có vai trò GUEST không
            if (UserStatus.GUEST.equals(user.getStatus())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền truy cập.");
            }



            // Xác thực người dùng bằng số điện thoại và mật khẩu
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getPhone(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtil.generateToken(authentication.getName());

            // Cập nhật trạng thái người dùng về "Đang hoạt động"
            user.setStatus(UserStatus.ACTIVE);
            userService.updateUser(user); // Lưu trạng thái mới vào cơ sở dữ liệu

            // Lấy quyền từ Authority
            String role = user.getRoles().stream()
                    .map(Role_User::getRole)
                    .map(Role::getName) // Giả định Role có phương thức getName()
                    .collect(Collectors.joining(", ")); // Ghép nhiều quyền nếu có

            // Tạo phản hồi
            AuthenticationResponse response = new AuthenticationResponse();
            response.setId(user.getId());
            response.setPhone(user.getPhone());
            response.setEmail(user.getEmail());
            response.setBirthDay(user.getBirthDay());
            response.setRole(role);
            response.setFullName(user.getFullName());
            response.setStatus(user.getStatus().name());
            response.setToken(jwt); // Trả JWT về client
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thông tin đăng nhập không chính xác.");
        }
    }


    //api sd
    @PutMapping("/{id}/logout")
    public ResponseEntity<Void> logout(@PathVariable Integer id) {
        User user = userService.findById(id); // Tìm người dùng theo ID
        if (user != null) {
            // Kiểm tra nếu người dùng đang ở trạng thái ACTIVE và có thể logout
            if (user.getStatus() == UserStatus.ACTIVE) {
                user.setStatus(UserStatus.INACTIVE); // Cập nhật trạng thái thành INACTIVE (đã đăng xuất)
                userService.updateUser(user); // Lưu thay đổi
                return ResponseEntity.ok().build(); // Trả về 200 OK
            } else {
                // Nếu người dùng đã đăng xuất trước đó
                return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Trả về 409 Conflict
            }
        }
        // Trả về 404 nếu không tìm thấy người dùng
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/fullName")
    public ResponseEntity<User> getUser(@RequestParam String fullName) {
        User user = userService.findByFullName(fullName);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }


    @GetMapping("/fullNameUser")
    public ResponseEntity<String> getUserFullName(@RequestParam Integer userId) {
        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user.getFullName());
    }

    @GetMapping("/{id}/email")
    public ResponseEntity<String> getEmailByUserId(@PathVariable("id") Integer userId) {
        try {
            String email = userService.getEmailByUserId(userId);
            return (email != null) ? ResponseEntity.ok(email) : ResponseEntity.notFound().build();
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(

            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String phone) {
        try {
            List<User> users = userService.searchUsers(fullName, phone);
            return ResponseEntity.ok(users);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }


    @GetMapping("/all")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserDTO> users = userService.getAllUsersWithRoles(pageable);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        try {
            // Giả sử bạn có service để tìm người dùng theo id
            User user = userService.findById(id);

            if (user != null) {
                return ResponseEntity.ok(user);  // Trả về thông tin người dùng
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }



    // API cập nhật thông tin người dùng
    @PutMapping("/update/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Integer userId, @RequestBody User updatedUser) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(null); // Trả về lỗi nếu userId không hợp lệ
        }

        try {
            // Lấy thông tin người dùng từ cơ sở dữ liệu
            User user = userService.findById(userId);

            // Cập nhật thông tin nếu người dùng tồn tại
            if (user != null) {
                user.setFullName(updatedUser.getFullName());
                user.setPhone(updatedUser.getPhone());
                user.setEmail(updatedUser.getEmail());
                user.setBirthDay(updatedUser.getBirthDay()); // Cập nhật ngày sinh

                // Lưu lại thông tin đã cập nhật
                User savedUser = userService.save(user);
                return ResponseEntity.ok(savedUser);
            } else {
                return ResponseEntity.notFound().build(); // Nếu không tìm thấy người dùng
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null); // Trả về lỗi server nếu có lỗi xảy ra
        }
    }



    @PutMapping("/change-password/{userId}")
    public ResponseEntity<?> changePassword(
            @PathVariable Integer userId,
            @RequestBody PasswordChangeRequest request) {

        try {
            userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok("Password changed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/updateAt")
    public ResponseEntity<User> updateUpdatedAt(@PathVariable Integer id) {
        try {
            User updatedUser = userService.updateUpdatedAt(id);
            return ResponseEntity.ok(updatedUser);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }



    //    // API thêm nhân viên
    @PostMapping("/managers/staffs/add")
    public ResponseEntity<String> addUser(@RequestBody StaffDTO staffDTO) {
        if (staffDTO.getRole() == null) {
            return ResponseEntity.badRequest().body("Quyền không hợp lệ.");
        }

        try {
            userService.addStaff(staffDTO);
            return ResponseEntity.ok("Nhân viên đã được thêm thành công.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi thêm nhân viên.");
        }
    }

    @GetMapping("/managers/staffs/all")
    public List<UserDTO> getStaffUsers() {
        return userService.getUsersByRole("STAFF");
    }

    @GetMapping("/managers/staffs/pages/all")
    public ResponseEntity<Page<UserDTO>> getStaffsByRoleName(
//            @PathVariable String roleName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<UserDTO> users = userService.getUsersByRoleName("STAFF", page, size);
        return ResponseEntity.ok(users);
    }



    // API khóa nhân viên
    @PutMapping("/managers/staffs/lock/{id}")
    public ResponseEntity<String> lockUser(@PathVariable Integer id) {
        try {
            userService.lockUser(id);
            return ResponseEntity.ok("Nhân viên đã bị khóa.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi khóa nhân viên.");
        }
    }

    // API mở khóa nhân viên
    @PutMapping("/managers/staffs/unlock/{id}")
    public ResponseEntity<String> unlockUser(@PathVariable Integer id) {
        try {
            userService.unlockUser(id);
            return ResponseEntity.ok("Nhân viên đã được mở khóa và đang hoạt động.");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


//    @PostMapping("/customers/register")
//    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
//        try {
//
//            // khi nhân viên hỗ trợ
//            // Kiểm tra xem User  đã tồn tại qua số điện thoại
//            Optional<User> existingUser = Optional.ofNullable(userService.findByPhone(request.getPhone()));
//
//            if (existingUser.isPresent()) {
//                User user = existingUser.get();
//
//                // Nếu User tồn tại và có status là GUEST
//                if (UserStatus.GUEST.equals(user.getStatus())) {
//                    // Cập nhật thông tin User
//                    user.setFullName(request.getFullName());
//                    user.setBirthDay(request.getBirthDay());
//                    user.setEmail(request.getEmail());
//
//                    // Mã hóa mật khẩu
//                    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//                    String encodedPassword = passwordEncoder.encode(request.getPassword());
//                    user.setPassword(encodedPassword);
//
//                    // Cập nhật trạng thái thành ACTIVE
//                    user.setStatus(UserStatus.ACTIVE);
//                    user.setCreatedAt(LocalDateTime.now());
//                    user.setUpdatedAt(LocalDateTime.now());
//
//                    userService.saveUser(user); // Lưu thay đổi vào database
//
//                    // Kiểm tra vai trò CUSTOMER, gán lại nếu chưa có
//                    Role customerRole = roleRepo.findByName("CUSTOMER");
//                    if (customerRole == null) {
//                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vai trò CUSTOMER không tồn tại.");
//                    }
//
//                    Role_User roleUser = roleUserRepo.findByUserId(user.getId())
//                            .orElseGet(() -> new Role_User(customerRole, user));
//                    roleUser.setRole(customerRole);
//                    roleUserRepo.save(roleUser);
//
//                    return ResponseEntity.ok("Cập nhật tài khoản GUEST thành công!");
//                }
//
//                // Nếu User tồn tại nhưng không phải GUEST
//                return ResponseEntity.status(HttpStatus.CONFLICT)
//                        .body("Số điện thoại đã được sử dụng bởi người dùng khác.");
//            }
//
//
//
//            // Nếu User không tồn tại, kiểm tra và tạo mới
//            if (userService.existsByPhone(request.getPhone())) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Số điện thoại đã được sử dụng.");
//            }
//
//            // Tạo đối tượng User
//            User user = new User();
//            user.setFullName(request.getFullName());
//            user.setBirthDay(request.getBirthDay());
//            user.setPhone(request.getPhone());
//            user.setEmail(request.getEmail());
//
//            // Mã hóa mật khẩu
//            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//            String encodedPassword = passwordEncoder.encode(request.getPassword());
//            user.setPassword(encodedPassword);
//
//            user.setStatus(UserStatus.ACTIVE);
//
//            user.setCreatedAt(LocalDateTime.now());
//            user.setUpdatedAt(LocalDateTime.now());
//
//
//            // Lấy đối tượng Role từ tên vai trò
//            Role customerRole  = roleRepo.findByName("CUSTOMER");
//
//            if (customerRole  == null) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vai trò CUSTOMER không tồn tại.");
//
//            }
//
//            // Lưu User vào cơ sở dữ liệu
//            userService.saveUser(user);
//
//
//            // Lưu vào bảng Role_User để gán role "CUSTOMER" cho user mới
//            Role_User roleUser = new Role_User(customerRole, user);
//            roleUserRepo.save(roleUser);
//            return ResponseEntity.ok("Tài khoản customer đã được tạo thành công!");
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã có lỗi xảy ra.");
//        }
//
//    }

    @PostMapping("/customers/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {

            // Kiểm tra xem User đã tồn tại qua số điện thoại
            Optional<User> existingUser = Optional.ofNullable(userService.findByPhone(request.getPhone()));

            if (existingUser.isPresent()) {
                User user = existingUser.get();

                // Kiểm tra xem user có vai trò GUEST không
                Role_User roleUser = roleUserRepo.findByUserId(user.getId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò người dùng"));

                if (roleUser.getRole().getName().equals("GUEST")) {
                    // Nếu là GUEST, cập nhật thông tin và vai trò thành CUSTOMER

                    // Cập nhật thông tin User
                    user.setFullName(request.getFullName());
                    user.setBirthDay(request.getBirthDay());
                    user.setEmail(request.getEmail());

                    // Mã hóa mật khẩu
                    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    String encodedPassword = passwordEncoder.encode(request.getPassword());
                    user.setPassword(encodedPassword);

                    // Cập nhật trạng thái thành ACTIVE
                    user.setStatus(UserStatus.ACTIVE);
                    user.setCreatedAt(LocalDateTime.now());
                    user.setUpdatedAt(LocalDateTime.now());

                    // Lưu thay đổi vào database
                    userService.saveUser(user);

                    // Cập nhật vai trò thành CUSTOMER
                    Role customerRole = roleRepo.findByName("CUSTOMER");
                    if (customerRole == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vai trò CUSTOMER không tồn tại.");
                    }

                    // Xóa vai trò cũ và tạo vai trò mới cho user
                    roleUserRepo.delete(roleUser);  // Xóa vai trò GUEST cũ
                    Role_User newRoleUser = new Role_User(customerRole, user);  // Tạo vai trò mới
                    roleUserRepo.save(newRoleUser);  // Lưu vai trò mới

                    // Cập nhật lại vai trò của user
                    roleUser.setRole(customerRole);
                    roleUserRepo.save(roleUser);

                    return ResponseEntity.ok("Cập nhật vai trò từ GUEST thành CUSTOMER thành công!");
                }

                // Nếu User tồn tại nhưng không phải GUEST
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Số điện thoại đã được sử dụng bởi người dùng khác.");
            }

            // Nếu User không tồn tại, kiểm tra và tạo mới
            if (userService.existsByPhone(request.getPhone())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Số điện thoại đã được sử dụng.");
            }

            // Tạo đối tượng User
            User user = new User();
            user.setFullName(request.getFullName());
            user.setBirthDay(request.getBirthDay());
            user.setPhone(request.getPhone());
            user.setEmail(request.getEmail());

            // Mã hóa mật khẩu
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            user.setPassword(encodedPassword);

            // Cập nhật trạng thái thành ACTIVE
            user.setStatus(UserStatus.ACTIVE);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            // Lấy đối tượng Role từ tên vai trò
            Role customerRole = roleRepo.findByName("CUSTOMER");

            if (customerRole == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vai trò CUSTOMER không tồn tại.");
            }

            // Lưu User vào cơ sở dữ liệu
            userService.saveUser(user);

            // Lưu vào bảng Role_User để gán role "CUSTOMER" cho user mới
            Role_User roleUser = new Role_User(customerRole, user);
            roleUserRepo.save(roleUser);

            return ResponseEntity.ok("Tài khoản customer đã được tạo thành công!");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã có lỗi xảy ra.");
        }
    }











//    @PutMapping("/managers/staffs/update/{id}")
//    public ResponseEntity<?> updateManager(@PathVariable Integer id, @RequestBody ManagerDTO managerDTO) {
//        try {
//            User user = userService.updateManager(id, managerDTO);
//            return ResponseEntity.ok(user); // trả về dữ liệu đã cập nhật
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi cập nhật quản lý.");
//        }
//    }

//    @GetMapping("/managers/customers/all")
//    public ResponseEntity<List<User>> getCustomers() {
//        try {
//            List<User> customers = userService.getCustomers();
//            return ResponseEntity.ok(customers);
//        }catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//
//    }


    @GetMapping("/managers/customers/all")
    public List<UserDTO> getCustomerUsers() {
        return userService.getUsersByRole("CUSTOMER");
    }

    @GetMapping("/managers/customers/pages/all")
    public ResponseEntity<Page<UserDTO>> getUsersByRoleName(
//            @PathVariable String roleName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<UserDTO> users = userService.getUsersByRoleName("CUSTOMER", page, size);
        return ResponseEntity.ok(users);
    }


    @PutMapping("/{userId}/lock")
    public ResponseEntity<String> lockUserAccount(@PathVariable Integer userId) {
        try {
            // Khóa tài khoản và cập nhật updatedAt
            User user = userService.lockUserAccount(userId);
            if (user != null) {
                // Gọi API updateUpdatedAt để cập nhật thời gian khóa
                userService.updateUpdatedAt(userId);
                return ResponseEntity.ok("Tài khoản đã bị khóa.");
            } else {
                return ResponseEntity.status(404).body("Không tìm thấy người dùng.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Có lỗi xảy ra khi khóa tài khoản.");
        }
    }

    // API để mở lại tài khoản sau khi khóa 3 ngày
    @PutMapping("/{userId}/unlock")
    public ResponseEntity<String> unlockUserAccount(@PathVariable Integer userId) {
        try {
            User user = userService.unlockUserAccountIfExpired(userId);
            if (user != null && user.getStatus() == UserStatus.OPENED) {
                return ResponseEntity.ok("Tài khoản đã được mở lại.");
            } else {
                return ResponseEntity.status(400).body("Tài khoản không bị khóa hoặc chưa đủ 3 ngày.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Có lỗi xảy ra khi mở lại tài khoản.");
        }
    }

    // API để lấy danh sách người dùng có trạng thái ACTIVE
//    @GetMapping("/active")
//    public ResponseEntity<List<User>> getActiveUsers() {
//        try {
//            List<User> activeUsers = userService.getActiveUsers();
//            return ResponseEntity.ok(activeUsers);
//        }catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//
//    }



}
