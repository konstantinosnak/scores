package gr.aueb.cf.scoresapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerRankingsDTO {
    private int ranking;
    private String playerName;
    private Long playerId;
    private int points;

}
