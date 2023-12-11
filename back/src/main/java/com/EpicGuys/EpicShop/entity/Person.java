package com.EpicGuys.EpicShop.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "_person")
public class Person {
	@Id
	@GeneratedValue
	private Long id;
	
	private String firstName;
	
	private String lastName;
	
	private String address;
	
	private String phone;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
}
