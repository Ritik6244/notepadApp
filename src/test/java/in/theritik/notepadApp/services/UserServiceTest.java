package in.theritik.notepadApp.services;

import in.theritik.notepadApp.entities.User;
import in.theritik.notepadApp.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - saveAdmin() Test Suite")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        testUser = new User("adminUser", "testPassword123");
        testUser.setId(new ObjectId());
    }

    // Test 1: Verify saveAdmin successfully saves user with encoded password
    @Test
    @DisplayName("Should save admin user with encoded password")
    void testSaveAdminEncodesPassword() {
        // Arrange
        String originalPassword = testUser.getPassword();

        // Act
        userService.saveAdmin(testUser);

        // Assert
        // Verify password is encoded (not the same as original)
        assertNotEquals(originalPassword, testUser.getPassword(),
                "Password should be encoded and different from original");
        assertTrue(passwordEncoder.matches(originalPassword, testUser.getPassword()),
                "Encoded password should match original password");
        verify(userRepository, times(1)).save(testUser);
    }

    // Test 2: Verify saveAdmin sets roles correctly
    @Test
    @DisplayName("Should set roles to [USER, ADMIN] for admin user")
    void testSaveAdminSetsCorrectRoles() {
        // Act
        userService.saveAdmin(testUser);

        // Assert
        assertNotNull(testUser.getRoles(), "Roles should not be null");
        assertEquals(2, testUser.getRoles().size(), "Admin user should have 2 roles");
        assertTrue(testUser.getRoles().contains("USER"), "Roles should contain USER");
        assertTrue(testUser.getRoles().contains("ADMIN"), "Roles should contain ADMIN");
        assertEquals(Arrays.asList("USER", "ADMIN"), testUser.getRoles(),
                "Roles should be [USER, ADMIN]");
    }

    // Test 3: Verify saveAdmin calls repository.save() exactly once
    @Test
    @DisplayName("Should call userRepository.save() exactly once")
    void testSaveAdminCallsRepositorySaveOnce() {
        // Act
        userService.saveAdmin(testUser);

        // Assert
        verify(userRepository, times(1)).save(testUser);
        verify(userRepository, only()).save(any());
    }

    // Test 4: Verify saveAdmin handles boundary case passwords
    @Test
    @DisplayName("Should throw IllegalArgumentException when password exceeds BCrypt 72-byte limit")
    void testSaveAdminWithPasswordExceedingBCryptLimit() {
        // Arrange
        // BCrypt has a 72-byte limit, so using 73-character password
        String tooLongPassword = "a".repeat(73);
        User userWithLongPassword = new User("testUser", tooLongPassword);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.saveAdmin(userWithLongPassword);
        }, "Should throw IllegalArgumentException when password exceeds 72 bytes");
    }

    // Test 5: Verify saveAdmin with empty password
    @Test
    @DisplayName("Should encode empty password string")
    void testSaveAdminWithEmptyPassword() {
        // Arrange
        testUser.setPassword("");

        // Act
        userService.saveAdmin(testUser);

        // Assert
        assertNotNull(testUser.getPassword(), "Password should not be null after encoding");
        assertNotEquals("", testUser.getPassword(), "Password should be encoded");
        verify(userRepository, times(1)).save(testUser);
    }

    // Test 6: Verify saveAdmin with special characters in password
    @Test
    @DisplayName("Should correctly encode password with special characters")
    void testSaveAdminWithSpecialCharactersPassword() {
        // Arrange
        String passwordWithSpecialChars = "p@ssw0rd!#$%&*";
        testUser.setPassword(passwordWithSpecialChars);

        // Act
        userService.saveAdmin(testUser);

        // Assert
        assertTrue(passwordEncoder.matches(passwordWithSpecialChars, testUser.getPassword()),
                "Encoded password should match original password with special characters");
        verify(userRepository, times(1)).save(testUser);
    }

    // Test 7: Verify saveAdmin preserves other user properties
    @Test
    @DisplayName("Should preserve all other user properties while setting password and roles")
    void testSaveAdminPreservesOtherProperties() {
        // Arrange
        String originalUserName = testUser.getUserName();
        ObjectId originalId = testUser.getId();

        // Act
        userService.saveAdmin(testUser);

        // Assert
        assertEquals(originalUserName, testUser.getUserName(), "Username should remain unchanged");
        assertEquals(originalId, testUser.getId(), "ID should remain unchanged");
    }

    // Test 8: Verify saveAdmin with long password (within BCrypt 72-byte limit)
    @Test
    @DisplayName("Should handle password up to 72 bytes (BCrypt limit)")
    void testSaveAdminWithLongPassword() {
        // Arrange
        // BCrypt has a 72-byte limit, so using 70-character password (70 bytes in ASCII)
        String longPassword = "a".repeat(70);
        testUser.setPassword(longPassword);

        // Act
        userService.saveAdmin(testUser);

        // Assert
        assertTrue(passwordEncoder.matches(longPassword, testUser.getPassword()),
                "Should correctly encode and verify password at BCrypt limit");
        verify(userRepository, times(1)).save(testUser);
    }

    // Test 9: Verify saveAdmin repository receives correct user object
    @Test
    @DisplayName("Should pass the correct user object to repository save method")
    void testSaveAdminPassesCorrectUserToRepository() {
        // Act
        userService.saveAdmin(testUser);

        // Assert
        verify(userRepository).save(argThat(user ->
                user.getUserName().equals("adminUser") &&
                user.getRoles().containsAll(Arrays.asList("USER", "ADMIN"))
        ));
    }

    // Test 10: Verify saveAdmin idempotency - calling multiple times
    @Test
    @DisplayName("Should handle multiple consecutive saveAdmin calls")
    void testSaveAdminMultipleCalls() {
        // Arrange
        String originalPassword = testUser.getPassword();

        // Act
        userService.saveAdmin(testUser);
        String firstEncodedPassword = testUser.getPassword();
        
        userService.saveAdmin(testUser);
        String secondEncodedPassword = testUser.getPassword();

        // Assert
        // Both should be encoded but different from original
        assertNotEquals(originalPassword, firstEncodedPassword);
        assertNotEquals(originalPassword, secondEncodedPassword);
        // Each call should encode the (now already encoded) password again
        assertNotEquals(firstEncodedPassword, secondEncodedPassword);
        verify(userRepository, times(2)).save(testUser);
    }

    // Test 11: Verify saveAdmin with numeric password
    @Test
    @DisplayName("Should encode numeric-only password")
    void testSaveAdminWithNumericPassword() {
        // Arrange
        testUser.setPassword("123456789");

        // Act
        userService.saveAdmin(testUser);

        // Assert
        assertTrue(passwordEncoder.matches("123456789", testUser.getPassword()),
                "Should correctly encode numeric password");
        assertEquals(2, testUser.getRoles().size());
        verify(userRepository, times(1)).save(testUser);
    }

    // Test 12: Verify saveAdmin with unicode characters in password
    @Test
    @DisplayName("Should encode password with unicode characters")
    void testSaveAdminWithUnicodePassword() {
        // Arrange
        testUser.setPassword("pässwörd€");

        // Act
        userService.saveAdmin(testUser);

        // Assert
        assertTrue(passwordEncoder.matches("pässwörd€", testUser.getPassword()),
                "Should correctly encode unicode password");
        verify(userRepository, times(1)).save(testUser);
    }
}

