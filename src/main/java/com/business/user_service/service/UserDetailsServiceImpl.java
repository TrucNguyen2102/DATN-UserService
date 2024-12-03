package com.business.user_service.service;

//import com.business.user_service.entity.Permission;
import com.business.user_service.entity.Role_User;
import com.business.user_service.entity.User;
import com.business.user_service.entity.Role;
import com.business.user_service.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private final UserService userService;
    @Autowired
    private UserRepo userRepo;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

//    @Override
//    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
//        User user = userService.findByPhone(phone);
//        if (user == null) {
//            throw new UsernameNotFoundException("User not found with phone: " + phone);
//        }
//        GrantedAuthority authority = new SimpleGrantedAuthority(user.getAuthority().getName());
//        return new org.springframework.security.core.userdetails.User(
//                user.getPhone(),
//                user.getPassword(),
//                Collections.singletonList(authority)
//        );
//    }

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        User user = userService.findByPhone(phone);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with phone: " + phone);
        }

        // Giả sử rằng user.getRoleUsers() trả về danh sách Role_User
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(Role_User::getRole) // Lấy Role từ Role_User
                .map(Role::getName) // Lấy tên của Role
                .map(SimpleGrantedAuthority::new) // Tạo đối tượng GrantedAuthority
                .collect(Collectors.toList());



        return new org.springframework.security.core.userdetails.User(
                user.getPhone(),
                user.getPassword(),
                authorities // Truyền danh sách quyền vào đây
        );
    }

//    @Override
//    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
//        User user = userService.findByPhone(phone);
//        if (user == null) {
//            throw new UsernameNotFoundException("User not found with phone: " + phone);
//        }
//
//        // Lấy danh sách quyền với tiền tố ROLE_
//        Collection<GrantedAuthority> authorities = user.getRoles().stream()
//                .map(Role_User::getRole) // Lấy Role từ Role_User
//                .map(Role::getName) // Lấy tên của Role
//                .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName)) // Thêm tiền tố "ROLE_"
//                .collect(Collectors.toList());
//
//
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getPhone(),
//                user.getPassword(),
//                authorities // Truyền danh sách quyền vào đây
//        );
//    }














}
