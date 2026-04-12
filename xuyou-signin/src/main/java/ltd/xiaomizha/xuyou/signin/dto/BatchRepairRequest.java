package ltd.xiaomizha.xuyou.signin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BatchRepairRequest {
    @JsonProperty("repairDates")
    private List<LocalDate> repairDates;
}
