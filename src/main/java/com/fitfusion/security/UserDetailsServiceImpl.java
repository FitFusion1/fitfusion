package com.fitfusion.security;

import com.fitfusion.mapper.UserMapper;
import com.fitfusion.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String inputValue)
            throws UsernameNotFoundException {
        Map<String, Object> condition = new HashMap<>();

        if (inputValue.contains("@")) {
            condition.put("type", "email");
        } else {
            condition.put("type", "username");
        }
        condition.put("identifier", inputValue);

        Optional<User> userOptional = userMapper.getUserWithRoleNames(condition);

        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return new SecurityUser(user);
    }
}
