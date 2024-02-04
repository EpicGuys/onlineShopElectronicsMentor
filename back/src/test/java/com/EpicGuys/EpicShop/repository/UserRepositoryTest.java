package com.EpicGuys.EpicShop.repository;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.EpicGuys.EpicShop.entity.User;
import com.EpicGuys.EpicShop.jpa.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {
	@Autowired
	private UserRepository userRepository;
	
	private final String email = "lolo@mail.ru";
	private final String password = "qwerty";
	
	@Test
	public void Save_ShouldReturnSavedEntity() {
		User user = User.builder()
				.email(email)
				.password(password)
				.build();
		
		User savedUser = userRepository.save(user);
		
		Assertions.assertNotNull(savedUser);
		Assertions.assertNotEquals(savedUser.getId(), 0L);
	}
	
	@Test
	public void GetUserByEmail_ShouldReturnUserByEmail() {
		User user = User.builder()
				.email(email)
				.password(password)
				.build();
		
		userRepository.save(user);
		Optional<User> searchedUser = userRepository.getUserByEmail(email);
		
		Assertions.assertTrue(searchedUser.isPresent());
		Assertions.assertEquals(email, searchedUser.get().getEmail());
	}
}
