package dao;

import dto.UserDTO;
import java.util.List;
import java.util.UUID;

public interface UserDAO {
    UUID insertUser(UserDTO user);
    UserDTO findUserById(UUID id);
    UserDTO findUserByUsername(String username);
    List<UserDTO> findAllUsers();
    void updateUser(UserDTO user);
    void deleteUser(UUID id);

    List<UserDTO> findUsersByMultipleCriteria(String usernamePattern, String emailPattern, String sortBy, String sortOrder);
    List<UserDTO> findRecentUsers(int days);
}