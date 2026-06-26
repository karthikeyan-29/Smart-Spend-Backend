package com.smartspend.backend.Controller;

import com.cloudinary.Cloudinary;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.smartspend.backend.DTO.CreateUserDTO;
import com.smartspend.backend.DTO.UserDTO;
import com.smartspend.backend.Entity.User;
import com.smartspend.backend.Service.CloudinaryService;
import com.smartspend.backend.Service.DeleteUserService;
import com.smartspend.backend.Service.UserService;
import com.smartspend.backend.Util.FirebaseTokenUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserService userService;
    @Autowired
    private final CloudinaryService cloudinaryService;

    private final DeleteUserService deleteUserService;
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    // Create User
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserDTO createUserDTO , @RequestHeader("Authorization") String authHeader) throws Exception {
        System.out.println("Im in");
        UserDTO createdUser = userService.createUser(createUserDTO , authHeader);
        return ResponseEntity.status(201).body(createdUser);
    }

    // Get All Users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        System.out.println("Im in");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 🔽 Get User By ID
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        System.out.println("I'm in getUserById");
        return ResponseEntity.ok(userService.getUserById(id));
    }

     //Update User
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        System.out.println("Im in");
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // Delete User
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        System.out.println("Im in");
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAccount(
            @RequestHeader("Authorization")String  authHeader
    )throws Exception{
        String uid=FirebaseTokenUtil.getUidFromToken(authHeader);
        deleteUserService.deleteUserCompletely(uid);
        return ResponseEntity.ok("Account completely deleted");
    }
    @GetMapping("/by-email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        UserDTO user = userService.getUserByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(user); // 200 with user data
    }
//    @PutMapping("/{id}/profile-image")
//     public ResponseEntity<?> updateProfileImageUrl(@PathVariable Long id , @RequestBody Map<String , String> request){
//        String url=request.get("imageurl");
//        if(url==null || url.isEmpty()){
//            return ResponseEntity.badRequest().body("Image url cannot be empty");
//        }
//        UserDTO dto =userService.updateProfileImageUrl( id, url);
//        return ResponseEntity.ok(dto);
//
//    }

    //update profile image in cloudinary
    @PutMapping("/profile-image")
    public ResponseEntity<UserDTO> updateUserProfileImage(
            @RequestHeader("Authorization") String token ,
            @RequestParam("image")MultipartFile file) throws Exception {
        String uid= FirebaseTokenUtil.getUidFromToken(token);
        String imageUrl=cloudinaryService.uploadProfileImage(uid , file);
        UserDTO updatedUser=userService.updateProfileImageUrl(uid , imageUrl);
        return  ResponseEntity.ok(updatedUser);

    }
    @PostMapping("/check")
    public ResponseEntity<UserDTO> checkUser(HttpServletRequest request) {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            System.out.println("Authorization Header: " + authorizationHeader);

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                System.out.println("Invalid or missing Authorization header");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String idToken = authorizationHeader.substring(7);
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String email = decodedToken.getEmail();
            System.out.println("Decoded email: " + email);

            UserDTO user = userService.getUserByEmail(email);
            if (user == null) {
                System.out.println("User not found in DB for email: " + email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // ✅ This is your desired path
            }

            System.out.println("User found: " + user.getName());
            return ResponseEntity.ok(user);
        } catch (FirebaseAuthException e) {
            System.out.println("Token invalid or expired: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500
        }
    }


}
