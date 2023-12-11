package com.EpicGuys.EpicShop.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.EpicGuys.EpicShop.entity.User;
import com.EpicGuys.EpicShop.jpa.UserRepository;

@Repository
public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userRepository.getUserByEmail(username);
		if(user.isEmpty()) {
			throw new UsernameNotFoundException("Could not find user");
		}
		return new UserDetailsImpl(user.get());
	}
}
