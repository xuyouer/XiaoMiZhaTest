package ltd.xiaomizha.xuyou.license.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户许可证关联信息DTO
 */
@Data
public class UserLicenseDTO {

    private Integer userId;
    private String username;
    private String licenseKey;
    private String licenseId;
    private String licenseType;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;

    public UserLicenseDTO() {
    }

    public UserLicenseDTO(Integer userId, String username, String licenseKey, String licenseId, String licenseType, String status, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.licenseKey = licenseKey;
        this.licenseId = licenseId;
        this.licenseType = licenseType;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = createdAt;
    }

}
