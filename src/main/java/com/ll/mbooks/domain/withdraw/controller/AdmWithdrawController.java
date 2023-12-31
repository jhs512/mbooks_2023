package com.ll.mbooks.domain.withdraw.controller;

import com.ll.mbooks.base.dto.RsData;
import com.ll.mbooks.base.rq.Rq;
import com.ll.mbooks.domain.withdraw.entity.WithdrawApply;
import com.ll.mbooks.domain.withdraw.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/adm/withdraw")
@RequiredArgsConstructor
public class AdmWithdrawController {
    private final WithdrawService withdrawService;
    private final Rq rq;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/applyList")
    public String showApplyList(Model model) {
        List<WithdrawApply> withdrawApplies = withdrawService.findAll();

        model.addAttribute("withdrawApplies", withdrawApplies);
        return "adm/withdraw/applyList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{withdrawApplyId}")
    public String applyDone(@PathVariable Long withdrawApplyId) {
        RsData withdrawRsData = withdrawService.withdraw(withdrawApplyId);

        return Rq.redirectWithMsg("/adm/withdraw/applyList", withdrawRsData);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{withdrawApplyId}/cancel")
    public String cancel(@PathVariable Long withdrawApplyId) {
        RsData withdrawRsData = withdrawService.cancelApply(withdrawApplyId);

        return Rq.redirectWithMsg("/adm/withdraw/applyList", withdrawRsData);
    }
}
