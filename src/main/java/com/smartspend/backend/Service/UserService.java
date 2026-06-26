package com.smartspend.backend.Service;


import com.google.api.gax.rpc.NotFoundException;
import com.smartspend.backend.DTO.CreateUserDTO;
import com.smartspend.backend.DTO.UserDTO;
import com.smartspend.backend.Entity.User;
import com.smartspend.backend.Repository.UserRepository;
import com.smartspend.backend.Util.FirebaseTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class UserService {

    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO createUser(CreateUserDTO createUserDTO , String header) throws Exception {
        User user = convertToEntity(createUserDTO , header);
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        return convertToDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    //Updating user income
    public void incrementIncome(String email , Double newIncome){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setMonthlyIncome(user.getMonthlyIncome()+newIncome);
        userRepository.save(user);


    }
    public void decrementIncome(String email , Double newIncome){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setMonthlyIncome(user.getMonthlyIncome()-newIncome);
        userRepository.save(user);


    }
    // UPDATE USER
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setMonthlyIncome(userDTO.getMonthlyIncome());
        user.setProfileImageUrl(userDTO.getProfileImageUrl());
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }
    public UserDTO getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToDTO)
                .orElse(null); //
    }


    //DELETE USER
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }
    //update profile image
    public UserDTO updateProfileImageUrl(String uid, String imageUrl) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setProfileImageUrl(imageUrl);
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }
    // DTO conversion
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
               user.getId(),
                user.getName() ,
                user.getEmail(),
                user.getMonthlyIncome(),
                user.getTotalBalance(),
                user.getProfileImageUrl()
        );
    }


    private User convertToEntity(CreateUserDTO dto , String header) throws Exception {
        String uid= FirebaseTokenUtil.getUidFromToken(header);
        return new User(
                null, // ID is auto-generated
                dto.getName(),
                dto.getEmail(),
                dto.getMonthlyIncome(),
                dto.getTotalBalance(),
                uid,
                dto.getProfileImageUrl()




        );
    }

}
