package com.codemaster.demo.controller;

import com.codemaster.demo.repository.RankingSemanalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class RankingController {

    private final RankingSemanalRepository rankingRepository;

    @GetMapping("/ranking")
    public String mostrarRanking(Model model) {

        model.addAttribute(
                "rankings",
                rankingRepository.findAllByOrderByPuntosAcumuladosDesc()
        );

        return "ranking";
    }
}
