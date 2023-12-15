package gr.aueb.cf.scoresapp.repository;

import gr.aueb.cf.scoresapp.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Team findTeamByTeamId(Long id);
    Team findPlayerByTeamName(String TeamName);

}
