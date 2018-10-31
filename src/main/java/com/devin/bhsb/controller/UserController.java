package com.devin.bhsb.controller;

import com.devin.bhsb.exception.ResourceNotFoundException;
import com.devin.bhsb.model.User;
import com.devin.bhsb.payload.*;
import com.devin.bhsb.repository.BookRepository;
import com.devin.bhsb.repository.BorrowRepository;
import com.devin.bhsb.repository.UserRepository;
import com.devin.bhsb.security.CurrentUser;
import com.devin.bhsb.security.UserPrincipal;
import com.devin.bhsb.service.BookService;
import com.devin.bhsb.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private BookService bookService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public UserSummery getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserSummery userSummery = new UserSummery(currentUser.getId(), currentUser.getUsername(), currentUser.getName());
        return userSummery;
    }

    @GetMapping("/user/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
        Boolean isAvailable = !userRepository.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/user/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/users/{username}")
    public UserProfile getUserProfile(@PathVariable(value = "username") String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        long bookCount = bookRepository.countByCreatedBy(user.getId());
        long borrowCount = borrowRepository.countByUserId(user.getId());

        UserProfile userProfile = new UserProfile(user.getId(), user.getUsername(), user.getName(), user.getCreatedAt(), borrowCount);

        return  userProfile;
    }

    @GetMapping("/users/{username}/books")
    public PagedResponse<BookResponse> getBooksCreatedBy(@PathVariable(value = "username") String username,
                                                         @CurrentUser UserPrincipal currentuser,
                                                         @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                         @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return bookService.getBooksCreatedBy(username, currentuser, page, size);
    }

    @GetMapping("/users/{username}/borrows")
    public PagedResponse<BookResponse> getBooksBorrowedBy(@PathVariable(value = "username") String username,
                                                          @CurrentUser UserPrincipal currentUser,
                                                          @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                          @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return bookService.getBooksBorrowedBy(username, currentUser, page, size);
    }
}
