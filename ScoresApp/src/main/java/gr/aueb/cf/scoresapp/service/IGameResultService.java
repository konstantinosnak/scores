package gr.aueb.cf.scoresapp.service;

import gr.aueb.cf.scoresapp.dto.GameResultInsertDTO;
import gr.aueb.cf.scoresapp.dto.GameResultUpdateDTO;
import gr.aueb.cf.scoresapp.model.GameResult;
import gr.aueb.cf.scoresapp.model.Player;
import gr.aueb.cf.scoresapp.model.Team;
import gr.aueb.cf.scoresapp.service.exceptions.EntitiesNotFoundException;
import gr.aueb.cf.scoresapp.service.exceptions.EntityNotFoundException;

import java.util.List;

public interface IGameResultService {
    List<GameResult> getAllGameResults() throws EntitiesNotFoundException;
    GameResult getGameResultById(Long id) throws EntityNotFoundException;
    GameResult insertGameResult(GameResultInsertDTO dto) throws Exception;
    GameResult updateGameResult(GameResultUpdateDTO dto) throws EntityNotFoundException;
    GameResult deleteGameResult(Long id) throws EntityNotFoundException;
//    GameResult saveScoreResult(GameResultInsertDTO dto, String username) throws Exception;

    List<GameResult> countPlayerGames(Player player);
    List<GameResult> countPlayerWins(Player player);
    List<GameResult> countPlayerLoses(Player player);

    List<GameResult> countTeamGames(Team team);
    List<GameResult> countTeamWins(Team team);
    List<GameResult> countTeamLoses(Team team);
}
