package com.business.user_service.controller;

import com.business.user_service.dto.*;
import com.business.user_service.entity.*;
import com.business.user_service.jwt.JwtUtil;
import com.business.user_service.repository.RoleRepo;
import com.business.user_service.repository.RoleUserRepo;
import com.business.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

//    @Autowired
//    private AuthorityService authorityService;

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

    //api sd
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest request) {
        try {

            // Tìm người dùng theo số điện thoại
            User user = userService.findByPhone(request.getPhone());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thông tin đăng nhập không chính xác.");
            }

            // Kiểm tra trạng thái tài khoản
            if (user.getStatus() == UserStatus.BLOCKED) { // Kiểm tra trạng thái
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tài khoản đã bị khóa.");
            }
            // Xác thực người dùng bằng số điện thoại và mật khẩu
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getPhone(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

//            // Lấy thông tin người dùng
//            User user = userService.findByPhone(request.getPhone());

            // Tạo JWT Token
//            String jwt = jwtUtil.generateToken(authentication);
            String jwt = jwtUtil.generateToken(authentication.getName());

            // Cập nhật trạng thái người dùng về "Đang hoạt động"
//            user.setStatus("Đang hoạt động");
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
            //response.setAuthority(authority);
            response.setRole(role);
            response.setFullName(user.getFullName());
//            user.setStatus((user.getStatus()));
            response.setStatus(user.getStatus().name());
            response.setToken(jwt); // Trả JWT về client
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thông tin đăng nhập không chính xác.");
        }
    }


//    @PutMapping("/{id}/logout")
//    public ResponseEntity<Void> logout(@PathVariable Integer id) {
//        User user = userService.findById(id); // Tìm người dùng theo ID
//        if (user != null) {
//            // Kiểm tra trạng thái người dùng trước khi cập nhật
////            if ((user.getStatus() == UserStatus.INACTIVE)) {
////                user.setStatus("Đã đăng xuất"); // Cập nhật trạng thái
//                user.setStatus(UserStatus.INACTIVE);
//                userService.updateUser(user); // Lưu thay đổi
//                return ResponseEntity.ok().build();
//            } else {
//                // Nếu trạng thái đã là "Đã đăng xuất"
//                return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409 Conflict
//            }
////        }
////        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//    }

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


    @GetMapping("/all")

    public ResponseEntity<List<UserDTO>> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsersWithRoles();
            return ResponseEntity.ok(users);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
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


//    @PostMapping("/admin/register")
//    @PreAuthorize("hasAuthority('ADMIN')") // Chỉ cho phép admin thực hiện
//    public ResponseEntity<?> registerOwner(@RequestBody RegisterRequest request) {
//        // Kiểm tra mật khẩu
//        if (!request.getPassword().equals(request.getConfirmPassword())) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mật khẩu không khớp.");
//        }
//
//        // Kiểm tra số điện thoại đã tồn tại
//        if (userService.existsByPhone(request.getPhone())) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Số điện thoại đã được sử dụng.");
//        }
//
//        // Kiểm tra email đã tồn tại
//        if (userService.existsByEmail(request.getEmail())) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email đã được sử dụng.");
//        }
//
//        // Tạo đối tượng User
//        User user = new User();
//        user.setFullName(request.getFullName());
//        user.setPhone(request.getPhone());
//        user.setEmail(request.getEmail());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setUpdatedAt(LocalDateTime.now());
//        user.setUpdatedAt(LocalDateTime.now());
//
//        // Thiết lập quyền cho owner
//        Authority authority = authorityService.findByName("OWNER");
//        if (authority == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quyền OWNER không tồn tại.");
//        }
//        //user.setAuthority(authority);
//
//        // Lưu người dùng
//        userService.saveUser(user);
//        return ResponseEntity.ok("Tài khoản owner đã được tạo thành công!");
//    }

//    @PostMapping("/customer/register")
//    @PreAuthorize("hasAuthority('CUSTOMER')") // Chỉ cho phép admin thực hiện
//    public ResponseEntity<?> registerCustomer(@RequestBody RegisterRequest request) {
//        // Kiểm tra mật khẩu
//        if (!request.getPassword().equals(request.getConfirmPassword())) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mật khẩu không khớp.");
//        }
//
//        // Kiểm tra số điện thoại đã tồn tại
//        if (userService.existsByPhone(request.getPhone())) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Số điện thoại đã được sử dụng.");
//        }
//
//        // Kiểm tra email đã tồn tại
//        if (userService.existsByEmail(request.getEmail())) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email đã được sử dụng.");
//        }
//
//        // Tạo đối tượng User
//        User user = new User();
//        user.setFullName(request.getFullName());
//        user.setPhone(request.getPhone());
//        user.setEmail(request.getEmail());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setUpdatedAt(LocalDateTime.now());
//        user.setUpdatedAt(LocalDateTime.now());
//
//        // Thiết lập quyền cho customer
//        Authority authority = authorityService.findByName("CUSTOMER");
//        if (authority == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quyền CUSTOMER không tồn tại.");
//        }
//        //user.setAuthority(authority);
//
//        // Lưu người dùng
//        userService.saveUser(user);
//        return ResponseEntity.ok("Tài khoản customer đã được tạo thành công!");
//    }

//    @GetMapping("/all")
//    public ResponseEntity<List<User>> getAllUsers() {
//        try {
//            List<User> users = userService.getAllUsers();
//            return ResponseEntity.ok(users); // Trả về danh sách người dùng kèm roles dưới dạng JSON
//        }catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.badRequest().build();
//        }
//
//    }



    @PostMapping("/customers/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            //        // Kiểm tra mật khẩu có khớp không
//        if (!request.getPassword().equals(request.getConfirmPassword())) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mật khẩu không khớp.");
//        }

            // Kiểm tra số điện thoại đã tồn tại
            if (userService.existsByPhone(request.getPhone())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Số điện thoại đã được sử dụng.");
            }

            // Kiểm tra email đã tồn tại
            if (userService.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email đã được sử dụng.");
            }

            // Tạo đối tượng User
            User user = new User();
            user.setFullName(request.getFullName());
            user.setPhone(request.getPhone());
            user.setEmail(request.getEmail());

            // Mã hóa mật khẩu
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            user.setPassword(encodedPassword);

            user.setStatus(UserStatus.ACTIVE);

            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());


            // Lấy đối tượng Role từ tên vai trò
            Role customerRole  = roleRepo.findByName("CUSTOMER");

            if (customerRole  == null) {
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










    @PutMapping("/staffs/update/{id}")
    public ResponseEntity<User> updateStaff(@PathVariable Integer id, @RequestBody User updatedUser) {
        // Kiểm tra giá trị id trước khi xử lý
        if (id == null) {
            return ResponseEntity.badRequest().build(); // Trả về 400 nếu id không hợp lệ
        }

        User user = userService.findById(id);
        if (user != null) {
            user.setFullName(updatedUser.getFullName());
            user.setPhone(updatedUser.getPhone());
            user.setEmail(updatedUser.getEmail());
           // user.setAuthority(updatedUser.getAuthority());
            userService.updateUser(user);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

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



}
