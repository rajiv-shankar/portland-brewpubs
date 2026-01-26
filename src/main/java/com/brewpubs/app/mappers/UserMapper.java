package com.brewpubs.app.mappers;

import com.brewpubs.app.models.User;
import org.apache.ibatis.annotations.*;

// ––––– Created by Rajiv Shankar on 1/9/26 @ 1:10 PM ––––– //

/**
 * UserMapper - MyBatis interface for USERS table operations*
 */
@Mapper
public interface UserMapper {

    // ========== READ OPERATIONS ==========

    /**
     * Find user by username (for login authentication)
     * @param username The username to search for
     * @return User if found, null if not
     */
    @Select("SELECT * FROM USERS WHERE username = #{username}")
    User getUserByUsername(String username);

    /**
     * Find user by ID
     * @param userId The user ID to search for
     * @return User if found, null if not
     */
    @Select("SELECT * FROM USERS WHERE user_id = #{userId}")
    User getUserById(Integer userId);

    // ========== CREATE OPERATIONS ==========

    /**
     * Insert a new user into the database
     * @param user The User object to insert (password should already be hashed!)
     * @return Number of rows affected (should be 1)
     *
     * NOTE: The password stored is the HASHED password, not plain text
     * The salt is stored separately for verification during login
     */
    @Insert("INSERT INTO USERS (username, salt, password, first_name, last_name) " +
            "VALUES (#{username}, #{salt}, #{password}, #{firstName}, #{lastName})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(User user);
}


