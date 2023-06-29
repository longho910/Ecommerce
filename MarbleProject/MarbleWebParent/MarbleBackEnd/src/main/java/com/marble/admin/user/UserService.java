package com.marble.admin.user;

import com.marble.common.entity.Role;
import com.marble.common.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> listAll() {
        return (List<User>) userRepo.findAll();
    }

    public List<Role> listRoles() {
        return (List<Role>)  roleRepo.findAll();
    }

    public void save(User userInForm) {
        boolean isUpdatingUser = (userInForm.getId() != null);

        if (isUpdatingUser) {
            User userInDB = userRepo.findById(userInForm.getId()).get();
            if (userInForm.getPassword().isEmpty()) {
                userInForm.setPassword(userInDB.getPassword());
            } else {
                encodePassword(userInForm);
            }
        } else {
            encodePassword(userInForm);
        }
        userRepo.save(userInForm);
    }

    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(Integer id, String email) {
        User userByEmail = userRepo.getUserByEmail(email);
        if (userByEmail == null) return true;

        boolean isCreatingNew = (id == null);
        if (isCreatingNew) {
            if (userByEmail != null) {
                return false;
            }
        } else {
            if (userByEmail.getId() != id) {
                return false;
            }
        }

        return true;
    }

    public User get(Integer id) throws UserNotFoundException {
        try {
            return userRepo.findById(id).get();
        }
        catch (NoSuchElementException ex) {
            throw new UserNotFoundException("Could not found any user with ID " + id);
        }

    }

    public void delete(Integer id) throws UserNotFoundException {
        Long countById = userRepo.countById(id);
        if (countById == null || countById == 0) {
            throw new UserNotFoundException("Could not found any user with ID " + id);
        }
        userRepo.deleteById(id);
    }

    public void updateUserEnabledStatus(Integer id, boolean enabled) {
        userRepo.updateEnabledStatus(id, enabled);
    }
}
