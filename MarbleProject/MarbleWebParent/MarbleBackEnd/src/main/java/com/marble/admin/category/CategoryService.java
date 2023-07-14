package com.marble.admin.category;

import com.marble.common.entity.Category;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class CategoryService {
    static final int ROOT_CATEGORIES_PER_PAGE = 4;
    @Autowired
    private CategoryRepository  repo;

    public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNumber, String sortDir,
                                     String keyword) {
        Sort sort = Sort.by("name");

        if (sortDir.equals("asc")) {
            sort = sort.ascending();
        }
        else if (sortDir.equals("desc")) {
            sort = sort.descending();
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, ROOT_CATEGORIES_PER_PAGE, sort);

        Page<Category> pageCategories = null;
        if (keyword != null && !keyword.isEmpty()) {
             pageCategories = repo.search(keyword, pageable);
        }
        else {
             pageCategories = repo.findRootCategories(pageable);
        }

        List<Category> rootCategories = pageCategories.getContent();

        pageInfo.setTotalElements(pageCategories.getTotalElements());
        pageInfo.setTotalPages(pageCategories.getTotalPages());

        int index = 1 + (pageNumber - 1) * ROOT_CATEGORIES_PER_PAGE;

        if (keyword != null && !keyword.isEmpty()) {
            List<Category> searchResult = pageCategories.getContent();
            for (Category category : searchResult) {
                category.setHasChildren(category.getChildren().size() > 0);
            }
            return searchResult;
        } else {
            return listHierarchicalCategories(rootCategories, sortDir, index);
        }
    }


    private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir, int index) {
        List<Category> hierarchicalCategories = new ArrayList<>();
//        int index = 1;

        for (Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.copyFull(rootCategory, index + " "
                    + rootCategory.getName()));

            Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);
            int subIndex = 1;

            for (Category subCategory : children) {
                String currentIndex = index + "." + subIndex;
                hierarchicalCategories.add(Category.copyFull(subCategory, currentIndex
                        + " " + subCategory.getName()));
                listSubHierarchicalCategories(hierarchicalCategories, subCategory, currentIndex, sortDir);
                subIndex++;
            }

            index++;
        }

        return hierarchicalCategories;
    }

    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories,
                                               Category parent, String parentIndex, String sortDir) {
        Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
        int index = 1;

        for (Category subCategory : children) {
            String currentIndex = parentIndex + "." + index;
            hierarchicalCategories.add(Category.copyFull(subCategory, currentIndex + " " + subCategory.getName()));

            listSubHierarchicalCategories(hierarchicalCategories, subCategory, currentIndex, sortDir);
            index++;
        }
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


    public Category save(Category category) {
        return repo.save(category);
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
        return sortSubCategories(children, "asc");
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare(Category cat1, Category cat2) {
                if (sortDir.equals("asc")) {
                    return cat1.getName().compareTo(cat2.getName());
                } else {
                    return cat2.getName().compareTo(cat1.getName());
                }
            }
        });
        sortedChildren.addAll(children);

        return sortedChildren;
    }

    // enable/disable function
    public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
        repo.updateEnabledStatus(id, enabled);
    }

    // delete category
    public void delete(Integer id) throws CategoryNotFoundException {
        Long countById = repo.countById(id);
        if (countById == null || countById == 0) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }
        repo.deleteById(id);
    }
}
