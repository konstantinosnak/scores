package gr.aueb.cf.scoresapp.service;

import gr.aueb.cf.scoresapp.dto.GameResultInsertDTO;
import gr.aueb.cf.scoresapp.dto.GameResultUpdateDTO;
import gr.aueb.cf.scoresapp.mapper.Mapper;
import gr.aueb.cf.scoresapp.model.GameResult;
import gr.aueb.cf.scoresapp.model.Player;
import gr.aueb.cf.scoresapp.model.Team;
import gr.aueb.cf.scoresapp.repository.GameResultRepository;
import gr.aueb.cf.scoresapp.repository.PlayerRepository;
import gr.aueb.cf.scoresapp.repository.TeamRepository;
import gr.aueb.cf.scoresapp.service.exceptions.EntitiesNotFoundException;
import gr.aueb.cf.scoresapp.service.exceptions.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Slf4j
public class GameResultServiceImpl implements IGameResultService{
    private final PlayerRepository playerRepository;
    private final GameResultRepository gameResultRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public GameResultServiceImpl(PlayerRepository playerRepository, GameResultRepository gameResultRepository, TeamRepository teamRepository) {
        this.playerRepository = playerRepository;
        this.gameResultRepository = gameResultRepository;
        this.teamRepository = teamRepository;
    }

    /**
     * Retrieves a list of all game results in the system.
     *
     * @return A list containing all game result entities.
     */
    @Override
    public List<GameResult> getAllGameResults() throws EntitiesNotFoundException {
        List<GameResult> results;
        try {
            results = gameResultRepository.findAll();
            if (results.isEmpty()) {
                log.error("No game results found.");
                throw new EntitiesNotFoundException(GameResult.class);
            }

        }catch (EntitiesNotFoundException ex) {
            log.error("Error in finding players");
            throw ex;
        }

        return results;
    }

    /**
     * Retrieves a specific game result by its id.
     *
     * @param id The unique id of the game result to retrieve.
     * @return he game result entity associated with the specified id.
     */

    @Override
    public GameResult getGameResultById(Long id) throws EntityNotFoundException {
        GameResult result = gameResultRepository.findGameResultByResultId(id);
        if (result == null) {
            throw new EntityNotFoundException(GameResult.class, id);
        }
        return result;
    }


    /**
     *  Inserts a new game result based on the provided GameResultInsertDTO.
     *  Calculates scores, checks existing entities, and persists the result.
     *
     * @param dto The data transfer object containing information for creating the new game result.
     * @return The newly created game result entity.
     */
    @Transactional
    @Override
    public GameResult insertGameResult(GameResultInsertDTO dto) throws Exception {
        GameResult finalResult;
        try {
            Player player1 = playerRepository.findPlayerByPlayerId(dto.getPlayer1Id());
            Player player2 = playerRepository.findPlayerByPlayerId(dto.getPlayer2Id());
            Team team1 = teamRepository.findTeamByTeamId(dto.getTeam1Id());
            Team team2 = teamRepository.findTeamByTeamId(dto.getTeam2Id());



            GameResult result = new GameResult();
            result.setGameDate(dto.getGameDate());
            result.setPlayer1(player1);
            result.setPlayer2(player2);
            result.setTeam1(team1);
            result.setTeam2(team2);
            result.setScorePlayer1(dto.getScorePlayer1());
            result.setScorePlayer2(dto.getScorePlayer2());

            checkExistingEntities(result);

            finalResult = calculateScores(result);
            gameResultRepository.saveAndFlush(finalResult);
        } catch (Exception ex) {
            log.error("Error in creating Game Result", ex);
            throw ex;
        }
        return finalResult;
    }

    /**
     * Updates an existing game result based on the provided GameResultUpdateDTO.
     * Calculates scores, checks existing entities, and persists the updated result.
     *
     * @param dto  The data transfer object containing information for updating the game result.
     * @return The updated game result entity.
     */
    @Transactional
    @Override
    public GameResult updateGameResult(GameResultUpdateDTO dto) throws EntityNotFoundException {
        GameResult updatedResult, existingResult, finalUpdatedResult;

        try {
            existingResult = gameResultRepository.findGameResultByResultId(dto.getGameResultId());
            if (existingResult == null) throw new EntityNotFoundException(GameResult.class, dto.getGameResultId());

            existingResult.setGameDate(dto.getGameDate());
            existingResult.setPlayer1(playerRepository.findPlayerByPlayerId(dto.getPlayer1Id()));
            existingResult.setPlayer2(playerRepository.findPlayerByPlayerId(dto.getPlayer2Id()));
            existingResult.setTeam1(teamRepository.findTeamByTeamId(dto.getTeam1Id()));
            existingResult.setTeam2(teamRepository.findTeamByTeamId(dto.getTeam2Id()));
            existingResult.setScorePlayer1(dto.getScorePlayer1());
            existingResult.setScorePlayer2(dto.getScorePlayer2());

            checkExistingEntities(existingResult);

            calculateScores(existingResult);

            finalUpdatedResult = gameResultRepository.saveAndFlush(existingResult);
        }catch (EntityNotFoundException ex) {
            log.error("Error in updating Game Result");
            throw ex;
        }
        return finalUpdatedResult;
    }


    /**
     * Deletes a game result based on the specified id.
     *
     * @param id The unique id of the game result to delete.
     * @return The deleted game result entity.
     */
    @Transactional
    @Override
    public GameResult deleteGameResult(Long id) throws EntityNotFoundException {
        GameResult result;
        try {
            result = gameResultRepository.findGameResultByResultId(id);
            if (result == null) throw new EntityNotFoundException(GameResult.class, id);
            gameResultRepository.delete(result);
        }catch (EntityNotFoundException ex) {
            log.error("Error in deleting Game Result");
            throw ex;
        }
        return result;
    }



    @Override
    public List<GameResult> countPlayerGames(Player player) {
        return gameResultRepository.findByPlayer1_PlayerIdOrPlayer2_PlayerId(player.getPlayerId(), player.getPlayerId());
    }

    @Override
    public List<GameResult> countPlayerWins(Player player) {
        return gameResultRepository.findByWinnerPlayer_PlayerId(player.getPlayerId());
    }

    @Override
    public List<GameResult> countPlayerLoses(Player player) {
        return gameResultRepository.findByLoserPlayer_PlayerId(player.getPlayerId());
    }

    @Override
    public List<GameResult> countTeamGames(Team team) {
        return gameResultRepository.findByTeam1_TeamIdOrTeam2_TeamId(team.getTeamId(), team.getTeamId());
    }

    @Override
    public List<GameResult> countTeamWins(Team team) {
        return gameResultRepository.findByWinningTeam_TeamId(team.getTeamId());
    }

    @Override
    public List<GameResult> countTeamLoses(Team team) {
        return gameResultRepository.findByLosingTeam_TeamId(team.getTeamId());
    }

    /**
     * Calculates scores and determines winners/losers for a given GameResult.
     * The winner, loser, winning team, and losing team attributes are set based on the scores.
     *
     * @param gameResult The GameResult entity for which scores and outcomes need to be calculated.
     * @return The updated GameResult entity with winner, loser, winning team, and losing team attributes set.
     */
    private GameResult calculateScores(GameResult gameResult) {
        if (gameResult.getScorePlayer1() > gameResult.getScorePlayer2()) {
            gameResult.setWinnerPlayer(gameResult.getPlayer1());
            gameResult.setLoserPlayer(gameResult.getPlayer2());
            gameResult.setWinningTeam(gameResult.getTeam1());
            gameResult.setLosingTeam(gameResult.getTeam2());
        } else if (gameResult.getScorePlayer1() < gameResult.getScorePlayer2()) {
            gameResult.setWinnerPlayer(gameResult.getPlayer2());
            gameResult.setLoserPlayer(gameResult.getPlayer1());
            gameResult.setWinningTeam(gameResult.getTeam2());
            gameResult.setLosingTeam(gameResult.getTeam1());
        } else {
            gameResult.setWinnerPlayer(null);
            gameResult.setLoserPlayer(null);
            gameResult.setWinningTeam(null);
            gameResult.setLosingTeam(null);
        }

        return gameResult;
    }

    /**
     * Checks for the existence of players and teams associated with a given GameResult.
     *
     * @param gameResult The GameResult entity containing player and team information to be checked
     */
    private void checkExistingEntities(GameResult gameResult) throws EntityNotFoundException {
        Player player1 = gameResult.getPlayer1();
        if (playerRepository.findPlayerByPlayerId(player1.getPlayerId()) == null) {
            throw new EntityNotFoundException(Player.class, player1.getPlayerId());
        }

        Player player2 = gameResult.getPlayer2();
        if (playerRepository.findPlayerByPlayerId(player2.getPlayerId()) == null) {
            throw new EntityNotFoundException(Player.class, player2.getPlayerId());
        }

        Team team1 = gameResult.getTeam1();
        if (teamRepository.findTeamByTeamId(team1.getTeamId()) == null) {
            throw new EntityNotFoundException(Team.class, team1.getTeamId());
        }

        Team team2 = gameResult.getTeam2();
        if (teamRepository.findTeamByTeamId(team2.getTeamId()) == null) {
            throw new EntityNotFoundException(Team.class, team1.getTeamId());
        }
    }
}
