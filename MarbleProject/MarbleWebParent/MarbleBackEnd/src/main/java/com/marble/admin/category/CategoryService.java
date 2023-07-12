package com.marble.admin.category;

import com.marble.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository  repo;

    public List<Category> listAll() {
        return (List<Category>) repo.findAll();
    }

    public Category save(Category category) {
        return repo.save(category);
    }

    public List<Category> listCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();

        Iterable<Category> categoriesInDB = repo.findAll();

        for (Category category : categoriesInDB) {
            if (category.getParent() == null) {
                categoriesUsedInForm.add(new Category(category.getName()));

                System.out.println(category.getName());

                addChildren(categoriesUsedInForm, category, 0);
            }
        }

        return categoriesUsedInForm;
    }

    private void addChildren(List<Category> list, Category parent, int subLevel) {

        int newSubLevel = subLevel + 1;
        Set<Category> children = parent.getChildren();

        for (Category subCategory : children) {
            String name = "";
            for (int i = 0; i < newSubLevel; i++) {
                name+= "--";
            }
            name += subCategory.getName();
            list.add(new Category(name));

            addChildren(list, subCategory, newSubLevel);
        }
    }
}
