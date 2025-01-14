package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }


  public User registerUser(User newUser) {
    
    checkIfUserExists(newUser);

    newUser.setStatus(UserStatus.ONLINE);
    newUser.setIsLoggedIn(true);
    newUser.setUserToken(UUID.randomUUID().toString());
    userRepository.save(newUser);
    userRepository.flush();
    
    // print user userToken to console
    System.out.println("User userToken---------------------------------: " + newUser.getUserToken());

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }


    public void loginUser(User userToLogin) {
        Optional<User> userOptional = userRepository.findById(userToLogin.getId());

        if (!userOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        }

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            user.setStatus(UserStatus.ONLINE);

            userRepository.save(user);
            userRepository.flush();
        }
    }

    public void logoutUser(User userToLogout) {
        Optional<User> userBeforeOptional = userRepository.findById(userToLogout.getId());
        if (!userBeforeOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        }

        if (userBeforeOptional.isPresent()) {
            User userBefore = userBeforeOptional.get();
            userToLogout.setStatus(UserStatus.OFFLINE);
            userToLogout.setPassword(userBefore.getPassword());
            userToLogout.setUsername(userBefore.getUsername());
            userToLogout.setBirthDay(userBefore.getBirthDay());
        }
        // saves the given entity but data is only persisted in the database once
        // flush() is called
        userToLogout = userRepository.save(userToLogout);
        userRepository.flush();
    }
  
    public User findByToken(String userToken) {
      try {
          System.out.println("Entering findByToken method.");
          List<User> users = userRepository.findAll();
  
          System.out.println("Searching for User with token: " + userToken + " in this list:");
          for (User user : users) {
              System.out.println("User userToken: " + user.getUserToken());
          }
  
          for (User user : users) {
              if (user.getUserToken().equals(userToken)) {
                  return user;
              }
          }
          System.out.println("User with token: " + userToken + " not found");
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");
      } catch (Exception e) {
          System.out.println("Exception in findByToken: " + e.getMessage());
          throw e;
      }
    }

  public User findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  public List<User> getAllUsers() {
    return this.userRepository.findAll();
  }

  public User updateUser(Long id, User user) throws Exception {
    // User reqUser = this.getUserById(id);
    User reqUser = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    if (!user.getUsername().isBlank()){
      checkIfUserExists(user);
      reqUser.setUsername(user.getUsername());
    }
    if (user.getBirthDay() != null){
      reqUser.setBirthDay(user.getBirthDay());
    }
    reqUser.setPassword(user.getPassword());
    reqUser.setUserToken(UUID.randomUUID().toString());
    reqUser.setStatus(UserStatus.ONLINE);
    reqUser.setIsLoggedIn(true);

    userRepository.save(reqUser);
    userRepository.flush();
    return reqUser;
  }

  public User getUserById(int id) {
    List<User> users = userRepository.findAll();
    User u = new User();
    for (int i=0; i<users.size(); i++){
      if (users.get(i).getId() == id){
        u = users.get(i);
      }
    }
    return u;
  }

  public User logoutUser(Long id){
    User u = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    u.setStatus(UserStatus.OFFLINE);
    u.setIsLoggedIn(false);
    return u;
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

    if (userByUsername != null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
            String.format("Username '%s' is already taken. Please choose a different one.", userToBeCreated.getUsername()));
    }
  }
}
