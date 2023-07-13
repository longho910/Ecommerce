package com.marble.admin.category;

import com.marble.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository  repo;

    public List<Category> listAll() {
        List<Category> rootCategories = repo.findRootCategories(Sort.by("name").ascending());
        return listHierarchicalCategories(rootCategories);
    }


    private List<Category> listHierarchicalCategories(List<Category> rootCategories) {
        List<Category> hierarchicalCategories = new ArrayList<>();
        int index = 1;

        for (Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.copyFull(rootCategory, index + " " + rootCategory.getName()));

            Set<Category> children = sortSubCategories(rootCategory.getChildren());
            int subIndex = 1;

            for (Category subCategory : children) {
                String currentIndex = index + "." + subIndex;
                hierarchicalCategories.add(Category.copyFull(subCategory, currentIndex + " " + subCategory.getName()));
                listSubHierarchicalCategories(hierarchicalCategories, subCategory, currentIndex);
                subIndex++;
            }

            index++;
        }

        return hierarchicalCategories;
    }

    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, String parentIndex) {
        Set<Category> children = sortSubCategories(parent.getChildren());
        int index = 1;

        for (Category subCategory : children) {
            String currentIndex = parentIndex + "." + index;
            hierarchicalCategories.add(Category.copyFull(subCategory, currentIndex + " " + subCategory.getName()));

            listSubHierarchicalCategories(hierarchicalCategories, subCategory, currentIndex);
            index++;
        }
    }




    public Category save(Category category) {
        return repo.save(category);
    }


    public List<Category> listCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();

        Iterable<Category> categoriesInDB = repo.findAll(Sort.by("name").ascending());
        int index = 1;

        for (Category category : categoriesInDB) {
            if (category.getParent() == null) {
                categoriesUsedInForm.add(Category.copyIdAndName(category.getId(), index + " " + category.getName()));
                addSubCategoriesUsedInForm(categoriesUsedInForm, category, Integer.toString(index), "  ");
                index++;
            }
        }

        return categoriesUsedInForm;
    }

    private void addSubCategoriesUsedInForm(List<Category> list, Category parent, String parentIndex, String indentation) {
        Set<Category> children = sortSubCategories(parent.getChildren());
        int index = 1;

        for (Category subCategory : children) {
            String currentIndex = parentIndex + "." + index;
            String indentedName = indentation + currentIndex + " " + subCategory.getName();
            list.add(Category.copyIdAndName(subCategory.getId(), indentedName));
            addSubCategoriesUsedInForm(list, subCategory, currentIndex, indentation + "  ");
            index++;
        }
    }



    public Category get(Integer id) throws CategoryNotFoundException {
        try {
            return repo.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }
    }

    public String checkUnique(Integer id, String name, String alias) {
        boolean isCreatingNew = (id == null || id == 0);

        Category categoryByName = repo.findByName(name);

        if (isCreatingNew) {
            if (categoryByName != null) {
                return "DuplicateName";
            } else {
                Category categoryByAlias = repo.findByAlias(alias);
                if (categoryByAlias != null) {
                    return "DuplicateAlias";
                }
            }
        }
        else {
            if (categoryByName != null && categoryByName.getId() != id) {
                return "DuplicateName";
            }
            Category categoryByAlias = repo.findByAlias(alias);
            if (categoryByAlias != null && categoryByAlias.getId() != id) {
                return "DuplicateAlias";
            }
        }
        return "OK";

    }

    private SortedSet<Category> sortSubCategories(Set<Category> children) {
        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare(Category cat1, Category cat2) {
                return cat1.getName().compareTo(cat2.getName());
            }
        });
        sortedChildren.addAll(children);

        return sortedChildren;
    }
}
