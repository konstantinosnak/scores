package gr.aueb.cf.scoresapp.repository;

import gr.aueb.cf.scoresapp.model.GameResult;
import gr.aueb.cf.scoresapp.model.Player;
import gr.aueb.cf.scoresapp.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameResultRepository extends JpaRepository<GameResult, Long> {
    List<GameResult> findByWinnerPlayer_PlayerId(Long playerId);
    List<GameResult> findByLoserPlayer_PlayerId(Long playerId);
    List<GameResult> findByPlayer1_PlayerIdOrPlayer2_PlayerId(Long playerId1, Long playerId2);
    List<GameResult> findByWinningTeam_TeamId(Long teamId);
    List<GameResult> findByLosingTeam_TeamId(Long teamId);
    List<GameResult> findByTeam1_TeamIdOrTeam2_TeamId(Long teamId1, Long teamId2);
    GameResult findGameResultByResultId(Long id);
    void deleteByPlayer1OrPlayer2(Player player1, Player player2);
    void deleteByTeam1OrTeam2(Team team1, Team team2);


}
