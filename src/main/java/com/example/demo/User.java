package com.example.demo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true,nullable = false)
    @NotBlank(message = "username are required")
    private String username;
    
    @Column(nullable = false)
    // @NotBlank(message = "password is mandatory")
    private String password;
    
    @Column(unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstname;
    
    @Column(name = "last_name")
    private String lastname;

    // @Column(name = "created_at")
    // private LocalDateTime createdAt;

    // @Column(name = "updated_at")
    // private LocalDateTime updatedAt;

    // @PrePersist
    // protected void onCreate(){
    //     createdAt = LocalDateTime.now();
    //     updatedAt = LocalDateTime.now();
    // }

    // @PrePersist
    // protected void onUpdate(){
    //     updatedAt  = LocalDateTime.now();
    // }
    // public User() {}

    // public User(String username, String password, String email, String firstname, String lastname){
    //     this.username = username;
    //     this.email = email;
    //     this.firstname = firstname;
    //     this.lastname = lastname;
    //     this.password = password;
    // }

}