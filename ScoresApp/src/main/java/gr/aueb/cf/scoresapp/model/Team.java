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
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long teamId;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @Lob
    @Column(name = "team_photo")
    private byte[] teamPhoto;

    @Column(name = "summary", length = 1500)
    private String summary;

    @Transient
    private String photoBase64;

    @OneToMany(mappedBy = "team1")
    private List<GameResult> gamesAsTeam1;

    @OneToMany(mappedBy = "team2")
    private List<GameResult> gamesAsTeam2;

    @OneToMany(mappedBy = "winningTeam")
    private List<GameResult> wonGames;

    @OneToMany(mappedBy = "losingTeam")
    private List<GameResult> lostGames;
}
