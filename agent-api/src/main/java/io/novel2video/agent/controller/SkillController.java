package io.novel2video.agent.controller;

import io.novel2video.agent.dto.*;
import io.novel2video.agent.entity.Skill;
import io.novel2video.agent.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Skill API Controller
 *
 * REST API for Skills management.
 * Skills are stored in MySQL with version control and usage statistics.
 */
@Tag(name = "Skill API", description = "Skills repository management")
@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @Operation(summary = "List all enabled skills")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SkillResponse>>> listSkills(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {

        List<Skill> skills;
        if (keyword != null && !keyword.isEmpty()) {
            skills = skillService.searchSkills(keyword);
        } else if (category != null && !category.isEmpty()) {
            skills = skillService.findByCategory(category);
        } else {
            skills = skillService.findAllEnabled();
        }

        List<SkillResponse> response = skills.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get popular skills")
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<SkillResponse>>> getPopularSkills(
            @RequestParam(defaultValue = "10") int limit) {

        List<Skill> skills = skillService.findPopularSkills(limit);
        List<SkillResponse> response = skills.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get skill by ID")
    @GetMapping("/{skillId}")
    public ResponseEntity<ApiResponse<SkillDetailResponse>> getSkill(
            @PathVariable String skillId,
            @RequestParam(defaultValue = "1.0.0") String version) {

        Skill skill = skillService.findBySkillIdAndVersion(skillId, version)
                .orElseThrow(() -> new RuntimeException("Skill not found: " + skillId));

        return ResponseEntity.ok(ApiResponse.success(toDetailResponse(skill)));
    }

    @Operation(summary = "Create a new skill")
    @PostMapping
    public ResponseEntity<ApiResponse<SkillResponse>> createSkill(
            @RequestBody CreateSkillRequest request) {

        Skill skill = new Skill()
                .setSkillId(request.getSkillId())
                .setName(request.getName())
                .setDescription(request.getDescription())
                .setCategory(request.getCategory())
                .setSkillContent(request.getContent())
                .setVersion(request.getVersion() != null ? request.getVersion() : "1.0.0")
                .setAuthor(request.getAuthor())
                .setTags(request.getTags())
                .setStatus(1)
                .setIsPublic(1)
                .setUseCount(0L)
                .setSuccessCount(0L);

        skill = skillService.createSkill(skill);
        return ResponseEntity.ok(ApiResponse.success(toResponse(skill)));
    }

    private SkillResponse toResponse(Skill skill) {
        return new SkillResponse(
                skill.getSkillId(),
                skill.getName(),
                skill.getDescription(),
                skill.getCategory(),
                skill.getVersion(),
                skill.getTags(),
                skill.getUseCount(),
                skill.getAvgRating()
        );
    }

    private SkillDetailResponse toDetailResponse(Skill skill) {
        return new SkillDetailResponse(
                skill.getSkillId(),
                skill.getName(),
                skill.getDescription(),
                skill.getCategory(),
                skill.getVersion(),
                skill.getTags(),
                skill.getSkillContent(),
                skill.getMetadata(),
                skill.getUseCount(),
                skill.getSuccessCount(),
                skill.getAvgRating()
        );
    }
}
