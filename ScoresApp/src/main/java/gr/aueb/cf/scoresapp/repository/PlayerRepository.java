package gr.aueb.cf.scoresapp.repository;

import gr.aueb.cf.scoresapp.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player,Long> {
    Player findPlayerByPlayerId(Long id);

}