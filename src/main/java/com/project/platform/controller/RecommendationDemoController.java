package com.project.platform.controller;

import com.project.platform.entity.ScenicInfo;
import com.project.platform.service.CollaborativeFilteringService;
import com.project.platform.mapper.RecommendationMapper;
import com.project.platform.mapper.ScenicInfoMapper;
import com.project.platform.dto.UserScenicScoreDTO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recommendation/demo")
public class RecommendationDemoController {

    @Resource
    private CollaborativeFilteringService collaborativeFilteringService;

    @Resource
    private RecommendationMapper recommendationMapper;

    @Resource
    private ScenicInfoMapper scenicInfoMapper;

    @Resource
    private com.project.platform.service.ScenicCategoryService scenicCategoryService;

    @GetMapping("/showUserRecommendation")
    public Map<String, Object> showUserRecommendation(@RequestParam Integer userId,
                                                      @RequestParam(defaultValue = "10") Integer topN) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<UserScenicScoreDTO> userScores = recommendationMapper.getScenicScoresByUser(userId);

            List<Map<String, Object>> userHistory = new ArrayList<>();
            for (UserScenicScoreDTO score : userScores) {
                ScenicInfo scenic = scenicInfoMapper.selectById(score.getScenicId());
                if (scenic != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("scenicId", scenic.getId());
                    item.put("scenicName", scenic.getName());
                    item.put("categoryId", scenic.getCategoryId());
                    item.put("categoryName", getCategoryName(scenic.getCategoryId()));
                    item.put("score", score.getTotalScore());
                    userHistory.add(item);
                }
            }

            List<ScenicInfo> recommendations = collaborativeFilteringService.recommendForUser(userId, topN);

            List<Map<String, Object>> recommendList = new ArrayList<>();
            for (ScenicInfo scenic : recommendations) {
                Map<String, Object> item = new HashMap<>();
                item.put("scenicId", scenic.getId());
                item.put("scenicName", scenic.getName());
                item.put("categoryId", scenic.getCategoryId());
                item.put("categoryName", getCategoryName(scenic.getCategoryId()));
                recommendList.add(item);
            }

            Set<Integer> userCategories = userHistory.stream()
                    .map(h -> (Integer) h.get("categoryId"))
                    .collect(Collectors.toSet());

            long matchCount = recommendList.stream()
                    .filter(r -> userCategories.contains(r.get("categoryId")))
                    .count();

            double matchRate = recommendList.isEmpty() ? 0.0 :
                    (double) matchCount / recommendList.size() * 100;

            Map<String, Long> userCategoryDist = userHistory.stream()
                    .collect(Collectors.groupingBy(
                            h -> (String) h.get("categoryName"),
                            Collectors.counting()
                    ));

            Map<String, Long> recommendCategoryDist = recommendList.stream()
                    .collect(Collectors.groupingBy(
                            r -> (String) r.get("categoryName"),
                            Collectors.counting()
                    ));

            result.put("success", true);
            result.put("userId", userId);
            result.put("userHistory", userHistory);
            result.put("userHistoryCount", userHistory.size());
            result.put("userCategoryDistribution", userCategoryDist);
            result.put("recommendations", recommendList);
            result.put("recommendationCount", recommendList.size());
            result.put("recommendCategoryDistribution", recommendCategoryDist);
            result.put("categoryMatchCount", matchCount);
            result.put("categoryMatchRate", Math.round(matchRate * 100.0) / 100.0);
            result.put("evaluation", matchRate >= 60 ? "excellent" : matchRate >= 40 ? "good" : "normal");

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Test failed: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    private String getCategoryName(Integer categoryId) {
        try {
            return scenicCategoryService.list().stream()
                    .filter(c -> c.getId().equals(categoryId))
                    .findFirst()
                    .map(c -> c.getName())
                    .orElse("Unknown");
        } catch (Exception e) {
            return "Unknown";
        }
    }

    @GetMapping(value = "/htmlDemo", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String htmlDemo(@RequestParam(required = false) Integer searchUserId) {
        try {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
            html.append("<title>Демонстрация рекомендательного алгоритма</title>");
            html.append("<style>");
            html.append("body{font-family:Arial,sans-serif;background:#f5f5f5;padding:20px;margin:0}");
            html.append(".container{max-width:1600px;margin:0 auto}");
            html.append("h1{color:#1976d2;text-align:center;margin-bottom:20px;font-size:28px}");
            html.append(".search-box{background:white;padding:15px;border-radius:8px;margin-bottom:20px;box-shadow:0 2px 4px rgba(0,0,0,0.1)}");
            html.append(".search-box input{padding:10px;border:1px solid #ddd;border-radius:4px;width:200px;margin-right:10px;font-size:14px}");
            html.append(".search-box button{padding:10px 24px;background:#1976d2;color:white;border:none;border-radius:4px;cursor:pointer;font-size:14px}");
            html.append(".search-box button:hover{background:#1565c0}");
            html.append(".user-row{background:white;margin-bottom:20px;padding:20px;border-radius:8px;box-shadow:0 2px 4px rgba(0,0,0,0.1)}");
            html.append(".user-header{display:flex;align-items:center;margin-bottom:15px;padding-bottom:10px;border-bottom:3px solid #1976d2}");
            html.append(".user-id{font-size:20px;font-weight:bold;color:#333;margin-right:15px}");
            html.append(".content-row{display:flex;gap:30px}");
            html.append(".history-section{flex:0 0 300px}");
            html.append(".recommendations-section{flex:1}");
            html.append(".section-title{font-size:16px;font-weight:bold;color:#555;margin-bottom:12px;padding-bottom:5px;border-bottom:2px solid #e0e0e0}");
            html.append(".item-list{display:flex;flex-wrap:wrap;gap:10px}");
            html.append(".item{background:#f8f9fa;padding:10px 15px;border-radius:6px;border-left:4px solid #1976d2;font-size:13px;min-width:200px}");
            html.append(".item-name{font-weight:600;color:#333;margin-bottom:4px}");
            html.append(".item-category{color:#666;font-size:12px}");
            html.append(".history-item{background:#e3f2fd;border-left-color:#2196f3}");
            html.append(".rec-item{background:#f1f8e9;border-left-color:#8bc34a}");
            html.append("</style></head><body>");

            html.append("<div class='container'>");
            html.append("<h1>Демонстрация рекомендательного алгоритма</h1>");

            html.append("<div class='search-box'>");
            html.append("<form method='get' action='/recommendation/demo/htmlDemo'>");
            html.append("<input type='number' name='searchUserId' placeholder='ID пользователя (2-20)' value='");
            html.append(searchUserId != null ? searchUserId : "").append("'>");
            html.append("<button type='submit'>Поиск</button>");
            html.append("</form></div>");

            List<Integer> displayUsers = new ArrayList<>();
            if (searchUserId != null && searchUserId >= 2 && searchUserId <= 20) {
                displayUsers.add(searchUserId);
            } else {
                displayUsers = Arrays.asList(7, 8, 9, 10, 11, 12);
            }

            for (Integer userId : displayUsers) {
                List<UserScenicScoreDTO> userScores = recommendationMapper.getScenicScoresByUser(userId);
                if (userScores.isEmpty() || userScores.size() < 2) continue;

                html.append("<div class='user-row'>");
                html.append("<div class='user-header'>");
                html.append("<div class='user-id'>Пользователь #").append(userId).append("</div>");
                html.append("</div>");

                html.append("<div class='content-row'>");
                
                html.append("<div class='history-section'>");
                html.append("<div class='section-title'>История предпочтений</div>");
                html.append("<div class='item-list'>");
                for (int i = 0; i < Math.min(3, userScores.size()); i++) {
                    ScenicInfo scenic = scenicInfoMapper.selectById(userScores.get(i).getScenicId());
                    if (scenic != null) {
                        html.append("<div class='item history-item'>");
                        html.append("<div class='item-name'>").append(scenic.getName()).append("</div>");
                        html.append("<div class='item-category'>").append(getCategoryName(scenic.getCategoryId())).append("</div>");
                        html.append("</div>");
                    }
                }
                html.append("</div></div>");

                html.append("<div class='recommendations-section'>");
                List<ScenicInfo> recommendations = collaborativeFilteringService.recommendForUser(userId, 6);
                html.append("<div class='section-title'>Рекомендации (Топ-").append(recommendations.size()).append(")</div>");
                html.append("<div class='item-list'>");
                for (int i = 0; i < recommendations.size(); i++) {
                    ScenicInfo scenic = recommendations.get(i);
                    html.append("<div class='item rec-item'>");
                    html.append("<div class='item-name'>").append(i + 1).append(". ").append(scenic.getName()).append("</div>");
                    html.append("<div class='item-category'>").append(getCategoryName(scenic.getCategoryId())).append("</div>");
                    html.append("</div>");
                }
                html.append("</div></div>");
                
                html.append("</div>");
                html.append("</div>");
            }

            html.append("</div></body></html>");
            return html.toString();

        } catch (Exception e) {
            return "<html><body><h1>Ошибка: " + e.getMessage() + "</h1></body></html>";
        }
    }
}
