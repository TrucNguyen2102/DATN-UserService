package com.business.user_service.service;

import com.business.user_service.dto.StaffDTO;
import com.business.user_service.entity.Authority;
import com.business.user_service.entity.User;
import com.business.user_service.exception.ResourceNotFoundException;
import com.business.user_service.repository.AuthorityRepo;
import com.business.user_service.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AuthorityRepo authorityRepo;
    @Autowired
    private AuthorityService authorityService;
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

        // Lấy đối tượng Authority từ tên quyền
        Authority authority = authorityRepo.findByName(staffDTO.getAuthority());
        user.setStatus("Đang hoạt động"); // Trạng thái mặc định
        // Kiểm tra xem authority có phải là null không
        if (authority == null) {
            throw new IllegalArgumentException("Quyền không hợp lệ: " + staffDTO.getAuthority());
        }
        user.setAuthority(authority);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepo.save(user);
    }

    @Override
    public void editUser(Integer id, StaffDTO staffDTO) {
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên."));
        user.setFullName(staffDTO.getFullName());
        user.setEmail(staffDTO.getEmail());
        user.setPhone(staffDTO.getPhone());
        user.setPassword(staffDTO.getPassword());
        // Lấy đối tượng Authority từ tên quyền
        Authority authority = authorityRepo.findByName(staffDTO.getAuthority());
        user.setAuthority(authority);
        userRepo.save(user);
    }

    @Override
    public void lockUser(Integer id) {
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên."));
        user.setStatus("Đã khóa"); // Thay đổi trạng thái thành "Đã khóa"
        userRepo.save(user);
    }

    @Override
    public void unlockUser(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên."));

        // Mở khóa và cập nhật trạng thái
        user.setStatus("Đang hoạt động"); // Đặt lại trạng thái thành "Đang hoạt động"
        userRepo.save(user);
    }


}
