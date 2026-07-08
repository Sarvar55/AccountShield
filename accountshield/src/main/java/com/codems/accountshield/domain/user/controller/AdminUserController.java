package com.codems.accountshield.domain.user.controller;

import com.codems.accountshield.common.constants.ApplicationConstants;
import com.codems.accountshield.domain.base.BaseResponse;
import com.codems.accountshield.domain.user.dto.AdminUserRoleRequest;
import com.codems.accountshield.domain.user.dto.AdminUserStatusRequest;
import com.codems.accountshield.domain.user.dto.UserResponse;
import com.codems.accountshield.domain.user.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping(value = "/admin", version = ApplicationConstants.API_VERSION)
    public ResponseEntity<BaseResponse<List<UserResponse>>> listUsers() {
        return ResponseEntity.ok(BaseResponse.success(adminUserService.listUsers()));
    }

    @PatchMapping(value = "/{id}/status/admin", version = ApplicationConstants.API_VERSION)
    public ResponseEntity<BaseResponse<UserResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody AdminUserStatusRequest request
    ) {
        return ResponseEntity.ok(BaseResponse.success(adminUserService.updateStatus(id, request)));
    }

    @PatchMapping(value = "/{id}/role/admin", version = ApplicationConstants.API_VERSION)
    public ResponseEntity<BaseResponse<UserResponse>> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody AdminUserRoleRequest request
    ) {
        return ResponseEntity.ok(BaseResponse.success(adminUserService.updateRole(id, request)));
    }
}
