package com.yahyaarhoune.transports.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Setter
@Getter
public class TrajetCreationRequestDTO {
    private String origine;
    private String destination;
    private LocalDateTime heureDepart;
    private LocalDateTime heureArrivee;
    private Integer vehiculeId;
    private Integer conducteurId;

}
