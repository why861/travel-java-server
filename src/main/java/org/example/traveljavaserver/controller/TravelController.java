package org.example.traveljavaserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.traveljavaserver.dto.ChatRequestDTO;
import org.example.traveljavaserver.service.TravelService;
import org.example.traveljavaserver.vo.TravelRecommendVO;
import org.springframework.web.bind.annotation.*;
import org.example.traveljavaserver.vo.Result;
import org.example.traveljavaserver.dto.TravelRequestDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
public class TravelController {
    private final TravelService travelService;

//    public TravelController(TravelService travelService) {
//        this.travelService = travelService;
//    }

    @GetMapping("/hello")
    public Result<String> hello () {
        return  Result.ok("hello world");
    }

    @PostMapping("/recommend")
    public Result<TravelRecommendVO> recommend (@Valid @RequestBody TravelRequestDTO travelRequestDTO) {

        System.out.println(travelRequestDTO.getCity());
        System.out.println(travelRequestDTO.getDays());
        System.out.println(travelRequestDTO.getBudget());
        TravelRecommendVO travelRecommendVO = travelService.recommend(travelRequestDTO.getCity(), travelRequestDTO.getDays(), travelRequestDTO.getBudget());
        return Result.ok(travelRecommendVO);
    }

    @PostMapping(value ="/chat", produces = "text/event-stream")
    public SseEmitter chat(@Valid @RequestBody ChatRequestDTO chatRequestDTO) {
        return travelService.chat(chatRequestDTO.getMessage());
    }
}
