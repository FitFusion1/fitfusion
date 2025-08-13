package com.fitfusion.web.controller;

import com.fitfusion.service.ExerciseListService;
import com.fitfusion.vo.Exercise;
import com.fitfusion.vo.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/exercise-list")
public class ExerciseListController {

    @Autowired
    private ExerciseListService exerciseListService;

    // 목록
    @GetMapping
    public String exerciseList(Model model) {
        model.addAttribute("exercises", exerciseListService.getAllExercises());
        model.addAttribute("countExercises", exerciseListService.countExercises());
        model.addAttribute("coachingExercises", exerciseListService.getAllCoachingExercises());
        model.addAttribute("countCoachingExercises", exerciseListService.countCoachingExercises());
        model.addAttribute("CountCategories", exerciseListService.getCategoryCount());
        model.addAttribute("CountEquipments", exerciseListService.getEquipmentCount());

        return "exercise-list/list";
    }

    /** 상세 → video.html (관련 동영상 없음) */
    @GetMapping("/{id}")
    public String video(@PathVariable int id, Model model) {
        Exercise exercise = exerciseListService.getExerciseById(id);
        if (exercise == null) {
            return "redirect:/exercise-list";
        }
        model.addAttribute("exercise", exercise);

        // 해당 운동의 최신 비디오 1건 (없을 수 있음)
        Video video = exerciseListService.getLatestByExerciseId(id);
        model.addAttribute("video", video);

        // 재생 경로: 없으면 샘플로 대체
        String videoPath = (video != null && video.getFilePath() != null && !video.getFilePath().isEmpty())
                ? video.getFilePath()
                : "/videos/sample.mp4";
        model.addAttribute("videoPath", videoPath);

        // "5분 30초" 같은 표시용 문자열
        String durationText = null;
        if (video != null && video.getDuration() > 0) {
            int total = video.getDuration();
            int min = total / 60;
            int sec = total % 60;
            durationText = min + "분 " + sec + "초";
        }
        model.addAttribute("videoDurationText", durationText);

        return "exercise-list/video";
    }
}
