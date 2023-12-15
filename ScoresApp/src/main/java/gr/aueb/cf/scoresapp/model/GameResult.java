package gr.aueb.cf.scoresapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "gameresults")
@EntityListeners(AuditingEntityListener.class)
public class GameResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    @Column(name = "gamedate")
    private LocalDate gameDate;


    @ManyToOne
    @JoinColumn(name = "player1_id")
    private Player player1;


    @ManyToOne
    @JoinColumn(name = "player2_id")
    private Player player2;


    @ManyToOne
    @JoinColumn(name = "team1_id")
    private Team team1;


    @ManyToOne
    @JoinColumn(name = "team2_id")
    private Team team2;


    @Column(name = "score_player1")
    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 99, message = "Score must be at most 99")
    private Integer scorePlayer1;


    @Column(name = "score_player2")
    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 99, message = "Score must be at most 99")
    private Integer scorePlayer2;


    @ManyToOne
    @JoinColumn(name = "winner_player_id")
    private Player winnerPlayer;


    @ManyToOne
    @JoinColumn(name = "loser_player_id")
    private Player loserPlayer;


    @ManyToOne
    @JoinColumn(name = "winning_team_id")
    private Team winningTeam;


    @ManyToOne
    @JoinColumn(name = "losing_team_id")
    private Team losingTeam;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

}
