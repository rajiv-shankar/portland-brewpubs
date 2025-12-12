package com.brewpubs.app.mappers;

/**
 * Created by Rajiv Shankar on 12/10/25 @ 2:58 PM.
 */

import com.brewpubs.app.models.Brewery;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * BreweryMapper - MyBatis interface for BREWERIES table operations
 *
 * HOW IT WORKS:
 * 1. @Mapper tells MyBatis to implement this interface
 * 2. Each method has a SQL annotation (@Select, @Insert, etc.)
 * 3. MyBatis generates the implementation at runtime
 * 4. #{fieldName} placeholders are replaced with Java object values
 *
 * COMPARISON TO SUPERDUPERDRIVE:
 * This is the same pattern used for UserMapper, NoteMapper, etc.
 */

@Mapper  // Tells Spring + MyBatis to create implementation
public interface BreweryMapper {

    // ========== READ OPERATIONS ==========

    /**
     * Get all breweries from database
     * @return List of all Brewery objects
     *
     * SQL: SELECT * FROM BREWERIES
     * Returns: Each row becomes a Brewery object
     */
    @Select("SELECT * FROM BREWERIES")    // ← DATABASE WORLD: what to fetch
    List<Brewery> getAllBreweries();      // ← JAVA WORLD: what to return

    /**
     * Find a brewery by its ID
     * @param breweryId The primary key to search for
     * @return Brewery if found, null if not
     *
     * #{breweryId} is replaced with the method parameter value
     */
    @Select("SELECT * FROM BREWERIES WHERE brewery_id = #{breweryId}")
    Brewery getBreweryById(Integer breweryId);

    /**
     * Count total number of breweries
     * @return Count as integer
     */
    @Select("SELECT COUNT(*) FROM BREWERIES")
    int getBreweryCount();

    // ========== CREATE OPERATIONS ==========

    /**
     * Insert a new brewery into database
     * @param brewery The Brewery object to insert
     * @return Number of rows affected (should be 1)
     *
     * #{name} extracts brewery.getName()
     * #{address} extracts brewery.getAddress()
     * #{signatureBeer} extracts brewery.getSignatureBeer()
     *
     * @Options sets the auto-generated ID back to the Brewery object
     */
    @Insert("INSERT INTO BREWERIES (name, address, signature_beer) " +
            "VALUES (#{name}, #{address}, #{signatureBeer})")
    @Options(useGeneratedKeys = true, keyProperty = "breweryId")
    int insert(Brewery brewery);

    // ========== UPDATE OPERATIONS ==========

    /**
     * Update an existing brewery
     * @param brewery Brewery with updated values (must have breweryId set)
     * @return Number of rows affected
     */
    @Update("UPDATE BREWERIES SET " +
            "name = #{name}, " +
            "address = #{address}, " +
            "signature_beer = #{signatureBeer} " +
            "WHERE brewery_id = #{breweryId}")
    int update(Brewery brewery);

    // ========== DELETE OPERATIONS ==========

    /**
     * Delete a brewery by ID
     * @param breweryId The ID of brewery to delete
     * @return Number of rows deleted
     */
    @Delete("DELETE FROM BREWERIES WHERE brewery_id = #{breweryId}")
    int delete(Integer breweryId);
}

