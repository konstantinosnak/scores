package gr.aueb.cf.scoresapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerReadOnlyDTO{
    private Long playerId;
    private String playerName;
    private String playerPhotoBase64;
}
