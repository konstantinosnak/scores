package gr.aueb.cf.scoresapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id")
    private Long playerId;

    @Column(name = "player_name", nullable = false)
    private String playerName;

    @Lob
    @Column(name = "player_photo")
    private byte[] playerPhoto;

    @Transient
    private String photoBase64;


    @OneToMany(mappedBy = "player1")
    private List<GameResult> gameResultsAsPlayer1;

    @OneToMany(mappedBy = "player2")
    private List<GameResult> gameResultsAsPlayer2;

    @OneToMany(mappedBy = "winnerPlayer")
    private List<GameResult> wonGames;

    @OneToMany(mappedBy = "loserPlayer")
    private List<GameResult> lostGames;
}
