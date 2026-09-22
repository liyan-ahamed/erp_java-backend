package com.java.erp.modules.schedule.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class SubmitPollResponseRequest {
    @NotNull
    @JsonProperty("option_index")
    private Integer optionIndex;
    public Integer getOptionIndex(){return optionIndex;}
    public void setOptionIndex(Integer optionIndex){this.optionIndex=optionIndex;}
}
