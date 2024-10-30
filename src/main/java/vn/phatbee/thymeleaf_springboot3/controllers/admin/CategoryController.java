package vn.phatbee.thymeleaf_springboot3.controllers.admin;

import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.util.StringUtils;
import vn.phatbee.thymeleaf_springboot3.entity.Category;
import vn.phatbee.thymeleaf_springboot3.models.CategoryModel;
import vn.phatbee.thymeleaf_springboot3.services.CategoryService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @RequestMapping("")
    public String all(ModelMap model){
        int count = (int) categoryService.count();
        int currentPage = 1;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.by("name"));
        Page<Category> resultPage = null;
        resultPage = categoryService.findAll(pageable);
        int totalPages = resultPage.getTotalPages();
        if(totalPages > 0) {
            int start = Math.max(1, currentPage-2);
            int end = Math.min(currentPage + 2, totalPages);
            if(totalPages > count) {
                if(end == totalPages) start = end - count;
                else if (start == 1) end = start + count;
            }
            List<Integer> pageNumbers = IntStream.rangeClosed(start, end)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers",pageNumbers);
        }
        model.addAttribute("categoryPage",resultPage);
        return "admin/categories/list";
    }

//    @GetMapping("/signup")
//    public String signup(Category category, ModelMap modelMap){
//        modelMap.addAttribute("category", category);
//        return "admin/category/add";
//    }

//    @PostMapping("/add")
//    public String add(@Valid Category category, BindingResult result, Model model){
//        if (result.hasErrors()) {
//            return "admin/category/add";
//        }
//        categoryService.save(category);
//        return "redirect:/admin/categories";
//    }

//    @GetMapping("/delete/{id}")
//    public String delete(@PathVariable("id") long id, Model model){
//        categoryService.deleteById(id);
//        return "redirect:/admin/categories";
//    }
//
//    @GetMapping("/edit/{id}")
//    public String edit(@PathVariable("id") long id, Model model){
//        Category category = categoryService.findById(id)
//                .orElseThrow(() -> new RuntimeException("Not found"));
//        model.addAttribute("category", category);
//        return "admin/category/update";
//    }
//
//    @PostMapping("/update/{id}")
//    public String update(@PathVariable("id") long id, @Valid Category category, BindingResult result,
//                         Model model){
//        if (result.hasErrors()){
//            return "admin/category/update";
//        }
//        categoryService.save(category);
//        return "admin/category/list";
//    }


    @GetMapping("/add")
    public String add(Model model){
        CategoryModel cateModel = new CategoryModel();
        cateModel.setIsEdit(false);
        model.addAttribute("category", cateModel);
        return "admin/categories/addOrEdit";
    }

    @PostMapping("/saveOrUpdate")
    public ModelAndView saveOrUpdate(ModelMap model, @Valid @ModelAttribute("category") CategoryModel cateModel, BindingResult result){
        if(result.hasErrors()){
            return new ModelAndView("admin/categories/addOrEdit");
        }
        Category entity = new Category();

        // Copy từ model sang enity
        BeanUtils.copyProperties(cateModel, entity);

        // Gọi hàm save trong service
        this.categoryService.save(entity);

        // Đưa thông báo về cho message
        String message = "";
        if (cateModel.getIsEdit()) {
            message = "Category edited successfully";
        } else {
            message = "Category is saved successfully";
        }
        model.addAttribute("message", message);

        // Redirect về URL Controller
        return new ModelAndView("redirect:/admin/categories", model);
    }
//
    @GetMapping("edit/{id}")
    public ModelAndView edit(ModelMap model, @PathVariable("id") Long categoryId ){
        Optional<Category> optCategory = categoryService.findById(categoryId);
        CategoryModel cateModel = new CategoryModel();

        // Kiểm tra sự tồn tại của category
        if(optCategory.isPresent()){
            Category entity = optCategory.get();

            // Copy từ entity sang Model
            BeanUtils.copyProperties(entity, cateModel);
            cateModel.setIsEdit(true);

            // Đẩy dữ liệu ra view
            model.addAttribute("category", cateModel);

            return new ModelAndView("admin/categories/addOrEdit", model);
        }
        model.addAttribute("message", "Category not found");
        return new ModelAndView("forward:/admin/categories", model);
    }

    @GetMapping("delete/{id}")
    public ModelAndView delete(ModelMap model, @PathVariable("id") Long categoryId){
        categoryService.deleteById(categoryId);
        model.addAttribute("message", "Category deleted successfully");
        return new ModelAndView("redirect:/admin/categories", model);
    }



//    @GetMapping("search")
//    public String search(ModelMap model, @RequestParam(name = "name", required = false) String name){
//        List<Category> list = null;
//
//        // Có nội dung truyền về không, name là tuỳ chọn khi require = false
//        if(StringUtils.hasText(name)){
//            list = categoryService.findByNameContaining(name);
//        } else{
//            list = categoryService.findAll();
//        }
//        model.addAttribute("categories", list);
//        return "admin/categories/search";
//
//    }

    @RequestMapping("searchpaginated")
    public String search(ModelMap model, @RequestParam(name = "name", required = false) String name,
                         @RequestParam("page") Optional<Integer> page,
                         @RequestParam("size") Optional<Integer> size){
        int count = (int) categoryService.count();
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(3);
        Pageable pageable = (Pageable) PageRequest.of(currentPage-1, pageSize, Sort.by("name"));
        Page<Category> resultPage = null;
        if (StringUtils.hasText(name)){
            resultPage = categoryService.findByNameContaining(name, pageable);
            model.addAttribute("name", name);
        } else{
            resultPage = categoryService.findAll(pageable);
        }

        int totalPages = resultPage.getTotalPages();
        if(totalPages > 0) {
            int start = Math.max(1, currentPage - 2);
            int end = Math.min(currentPage + 2, totalPages);
            if (totalPages > count) {
                if (end == totalPages) start = end - count;
                else if (start == 1) end = start + count;
            }
            List<Integer> pageNumbers = IntStream.rangeClosed(start, end)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers",pageNumbers);
        }
        model.addAttribute("categoryPage",resultPage);
        return "admin/categories/list";
    }

}
