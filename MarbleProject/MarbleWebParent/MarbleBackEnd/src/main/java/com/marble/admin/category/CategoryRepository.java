package com.marble.admin.category;

import com.marble.common.entity.Category;
import org.springframework.data.domain.Sort;
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

    public Category findByName(String name);

    public Category findByAlias(String alias);


}
