package gr.aueb.cf.scoresapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerStatsDTO {
    private Long playerId;
    private int totalGames;
    private int totalWins;
    private int totalLoses;
    private int totalTies;
    private double winPercentage;

}
