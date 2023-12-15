package gr.aueb.cf.scoresapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameResultUpdateDTO{
    private Long gameResultId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate gameDate;
    private Long player1Id;
    private Long player2Id;
    private Long team1Id;
    private Long team2Id;
    private Integer scorePlayer1;
    private Integer scorePlayer2;
//    private Long winnerPlayerId;
//    private Long loserPlayerId;
//    private Long winningTeamId;
//    private Long losingTeamId;

}
