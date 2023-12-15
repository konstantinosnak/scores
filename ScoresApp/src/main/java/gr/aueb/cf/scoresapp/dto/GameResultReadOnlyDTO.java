package gr.aueb.cf.scoresapp.dto;

import gr.aueb.cf.scoresapp.model.Player;
import gr.aueb.cf.scoresapp.model.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameResultReadOnlyDTO {
    private Long gameResultId;
    private LocalDate gameDate;
    private Player player1;
    private Player player2;
    private Team team1;
    private Team team2;
    private Integer scorePlayer1;
    private Integer scorePlayer2;
    private Player winnerPlayer;
    private Player loserPlayer;
    private Team winningTeam;
    private Team losingTeam;
    private String  createdBy;
    private String lastModifiedBy;
}
