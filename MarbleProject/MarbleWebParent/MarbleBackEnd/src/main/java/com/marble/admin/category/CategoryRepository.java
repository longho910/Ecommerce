package com.marble.admin.category;

import com.marble.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer>,
        PagingAndSortingRepository<Category, Integer> {

    // sort findAll
    @Override
    public Iterable<Category> findAll(Sort sort);

    @Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
    public List<Category> findRootCategories(Sort sort);
    @Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
    public Page<Category> findRootCategories(Pageable pageable);

    // used for delete categories
    public Long countById(Integer id);

    public Category findByName(String name);

    public Category findByAlias(String alias);

    @Query("UPDATE Category cat SET cat.enabled = ?2 WHERE cat.id=?1")
    @Modifying
    public void updateEnabledStatus(Integer id, boolean enabled);


}
