package com.content_management_system.lms.features.category.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.content_management_system.lms.features.category.dto.CourseCategoryResponse;
import com.content_management_system.lms.features.category.dto.CreateCourseCategoryRequest;
import com.content_management_system.lms.features.category.dto.UpdateCourseCategoryRequest;
import com.content_management_system.lms.features.category.mapper.CourseCategoryMapper;
import com.content_management_system.lms.features.category.service.CourseCategoryService;
import com.content_management_system.lms.features.category.dto.BulkDeleteRequest;
import com.content_management_system.lms.shared.entity.Course;
import com.content_management_system.lms.shared.entity.CourseCategory;
import com.content_management_system.lms.shared.entity.Lesson;
import com.content_management_system.lms.shared.entity.Module;
import com.content_management_system.lms.shared.entity.Quiz;
import com.content_management_system.lms.shared.entity.Tenant;
import com.content_management_system.lms.shared.exception.BadRequestException;
import com.content_management_system.lms.shared.exception.ResourceNotFoundException;
import com.content_management_system.lms.shared.repository.CategoryRepository;
import com.content_management_system.lms.shared.repository.CourseRepository;
import com.content_management_system.lms.shared.repository.LessonRepository;
import com.content_management_system.lms.shared.repository.ModuleRepository;
import com.content_management_system.lms.shared.repository.QuizRepository;
import com.content_management_system.lms.shared.repository.TenantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseCategoryServiceImpl implements CourseCategoryService {

    private final CategoryRepository courseCategoryRepo;
    private final TenantRepository tenantRepo;
    private final CourseRepository courseRepo;
    private final ModuleRepository moduleRepo;
    private final LessonRepository lessonRepo;
    private final QuizRepository quizRepo;
    @Override
    public CourseCategoryResponse create(CreateCourseCategoryRequest request) {
        CourseCategory courseCategory = new CourseCategory();
        courseCategory.setName(request.getName());
        courseCategory.setDescription(request.getDescription());
        Tenant tenant = tenantRepo.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with: " + request.getTenantId()));
        courseCategory.setTenant(tenant);
        CourseCategory savedCC = courseCategoryRepo.save(courseCategory);
        return CourseCategoryMapper.toResponse(savedCC);
    }

    @Override
    public List<CourseCategoryResponse> findAll(Long tenantId) {
        List<CourseCategory> categories;
        if (tenantId != null) {
            categories = courseCategoryRepo.findAll().stream()
                    .filter(category -> category.getTenant() != null 
                            && category.getTenant().getId().equals(tenantId))
                    .collect(Collectors.toList());
        } else {
            categories = courseCategoryRepo.findAll();
        }
        return categories.stream()
                .map(CourseCategoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CourseCategoryResponse findById(Long id) {
    	CourseCategory courseCategory = courseCategoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course Category not found with id: " + id));
        return CourseCategoryMapper.toResponse(courseCategory);
    }

    @Override
    public CourseCategoryResponse update(Long id, UpdateCourseCategoryRequest request) {
    	CourseCategory existingCC = courseCategoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course Category not found with id: " + id));

        existingCC.setName(request.getName());
        existingCC.setDescription(request.getDescription());

        CourseCategory updatedCC = courseCategoryRepo.save(existingCC);
        return CourseCategoryMapper.toResponse(updatedCC);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        CourseCategory category = courseCategoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course Category not found with id: " + id));
        
        long courseCount = courseRepo.countByCategoryId(id);
        if (courseCount > 0) {
            throw new BadRequestException(
                String.format("Cannot delete category '%s' because it is associated with %d course(s). " +
                    "Please remove or reassign all courses from this category before deleting it.", 
                    category.getName(), courseCount)
            );
        }
        
        courseCategoryRepo.deleteById(id);
    }

    @Override
    @Transactional
    public void bulkDelete(BulkDeleteRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            throw new BadRequestException("No category IDs provided for deletion");
        }

        List<CourseCategory> categories = courseCategoryRepo.findAllById(request.getIds());
        
        if (categories.size() != request.getIds().size()) {
            throw new ResourceNotFoundException("One or more categories not found");
        }

        // Check for associated courses
        List<String> categoriesWithCourses = new java.util.ArrayList<>();
        for (CourseCategory category : categories) {
            long courseCount = courseRepo.countByCategoryId(category.getId());
            if (courseCount > 0) {
                categoriesWithCourses.add(String.format("'%s' (%d course(s))", category.getName(), courseCount));
            }
        }

        if (!categoriesWithCourses.isEmpty()) {
            throw new BadRequestException(
                String.format("Cannot delete the following categories because they are associated with courses: %s. " +
                    "Please remove or reassign all courses from these categories before deleting them, or use force delete instead.",
                    String.join(", ", categoriesWithCourses))
            );
        }

        // Delete all categories
        courseCategoryRepo.deleteAll(categories);
    }

    @Override
    @Transactional
    public void forceDelete(BulkDeleteRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            throw new BadRequestException("No category IDs provided for deletion");
        }

        List<CourseCategory> categories = courseCategoryRepo.findAllById(request.getIds());
        
        if (categories.size() != request.getIds().size()) {
            throw new ResourceNotFoundException("One or more categories not found");
        }

        // Collect all courses, modules, lessons, and quizzes to delete
        List<Course> allCourses = new java.util.ArrayList<>();
        List<Module> allModules = new java.util.ArrayList<>();
        List<Lesson> allLessons = new java.util.ArrayList<>();
        List<Quiz> allQuizzes = new java.util.ArrayList<>();

        for (CourseCategory category : categories) {
            // Find all courses for this category
            List<Course> courses = courseRepo.findAllByCategoryId(category.getId());
            allCourses.addAll(courses);

            // For each course, find all modules
            for (Course course : courses) {
                List<Module> modules = moduleRepo.findAllByCourseId(course.getId());
                allModules.addAll(modules);

                // For each module, find all lessons and quizzes
                for (Module module : modules) {
                    List<Lesson> lessons = lessonRepo.findAllByModuleId(module.getId());
                    allLessons.addAll(lessons);
                    
                    List<Quiz> quizzes = quizRepo.findAllByModuleIdWithSoftDelete(module.getId());
                    allQuizzes.addAll(quizzes);
                }
            }
        }

        // Delete in reverse order: lessons -> quizzes -> modules -> courses -> categories
        lessonRepo.deleteAll(allLessons);
        quizRepo.deleteAll(allQuizzes);
        moduleRepo.deleteAll(allModules);
        courseRepo.deleteAll(allCourses);
        courseCategoryRepo.deleteAll(categories);
    }
}