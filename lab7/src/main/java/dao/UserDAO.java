package dao;

import dto.UserDTO;
import java.util.List;
import java.util.UUID;

public interface UserDAO {
    UserDTO findUserByUsername(String username);
    List<UserDTO> findAllUsers();
    UserDTO findUserById(UUID id);
    UUID insertUser(UserDTO user);
    boolean updateUser(UserDTO user);
    boolean deleteUser(UUID id);
    List<UserDTO> findUsersByMultipleCriteria(String usernamePattern, String emailPattern,
                                              String sortBy, String sortOrder);
    List<UserDTO> findRecentUsers(int days);
}