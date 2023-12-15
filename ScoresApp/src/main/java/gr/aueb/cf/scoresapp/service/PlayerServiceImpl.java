package gr.aueb.cf.scoresapp.service;

import gr.aueb.cf.scoresapp.dto.PlayerInsertDTO;
import gr.aueb.cf.scoresapp.dto.PlayerRankingsDTO;
import gr.aueb.cf.scoresapp.dto.PlayerStatsDTO;
import gr.aueb.cf.scoresapp.dto.PlayerUpdateDTO;
import gr.aueb.cf.scoresapp.mapper.Mapper;
import gr.aueb.cf.scoresapp.model.GameResult;
import gr.aueb.cf.scoresapp.model.Player;
import gr.aueb.cf.scoresapp.repository.GameResultRepository;
import gr.aueb.cf.scoresapp.repository.PlayerRepository;
import gr.aueb.cf.scoresapp.service.exceptions.EntitiesNotFoundException;
import gr.aueb.cf.scoresapp.service.exceptions.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class PlayerServiceImpl implements IPlayerService{
    private final PlayerRepository playerRepository;
    private final GameResultRepository gameResultRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository, GameResultRepository gameResultRepository) {
        this.playerRepository = playerRepository;
        this.gameResultRepository = gameResultRepository;
    }

    /**
     *  Retrieves a list of all players.
     *
     * @return A list containing all player entities.
     */
    @Override
    public List<Player> getAllPlayers() throws EntitiesNotFoundException {
        List<Player> players;
        try {
            players = playerRepository.findAll();
            if (players.isEmpty()) throw new EntitiesNotFoundException(Player.class);
            for (Player player : players) {
                if (player.getPlayerPhoto() != null) {
                    player.setPhotoBase64(Base64.getEncoder().encodeToString(player.getPlayerPhoto()));
                }
            }

        }catch (EntitiesNotFoundException ex) {
            log.error("Error in finding players");
            throw ex;
        }
        return players;
    }

    /**
     * Retrieves a player by their id.
     *
     * @param id the id of the player
     * @return The player entity associated with the id
     */
    @Override
    public Player getPlayerById(Long id) throws EntityNotFoundException {
        Player player = playerRepository.findPlayerByPlayerId(id);
        if (player == null) {
            throw new EntityNotFoundException(Player.class, id);
        }
        return player;
    }

    /**
     * Retrieves a player by their id along with the player's photo in Base64 format.
     *
     * @param playerId id of the player
     * @return The player entity with the associated photo in the Base64 format.
     */
    @Override
    public Player getPlayerWithPhotoById(Long playerId) throws EntityNotFoundException {
        Player player = playerRepository.findPlayerByPlayerId(playerId);
        if (player == null) {
            throw new EntityNotFoundException(Player.class, playerId);
        }

        player.setPhotoBase64(Base64.getEncoder().encodeToString(player.getPlayerPhoto()));
        return player;
    }

    /**
     * Inserts a new player based on the provided PlayerInsertDTO.
     *
     * @param dto The data transfer object containing information for creating the new player.
     * @return The newly created player entity.
     */
    @Transactional
    @Override
    public Player insertPlayer(PlayerInsertDTO dto) throws Exception {
        Player player = new Player();
        try {
            player.setPlayerName(dto.getPlayerName());
            player.setPlayerPhoto(Mapper.multipartFileToByteArray(dto.getPlayerPhoto()));
            playerRepository.saveAndFlush(player);
            if (player.getPlayerId() == null) throw new Exception("Insert Error Exception");
        }catch (Exception ex) {
            log.error("Error in inserting Player");
            throw ex;
        }
        return player;
    }

    /**
     * Updates an existing player in the system based on the provided PlayerUpdateDTO.
     *
     * @param dto The data transfer object containing information for updating the player.
     * @return The updated player entity.
     */
    @Transactional
    @Override
    public Player updatePlayer(PlayerUpdateDTO dto) throws EntityNotFoundException {
        Player updatedPlayer;
        Player existingPlayer;
        try {
            existingPlayer = playerRepository.findPlayerByPlayerId(dto.getPlayerId());
            if (existingPlayer == null) throw new EntityNotFoundException(Player.class, dto.getPlayerId());

            existingPlayer.setPlayerName(dto.getPlayerName());
            if (dto.getPlayerPhoto() != null && dto.getPlayerPhoto().getSize() !=0) {
                existingPlayer.setPlayerPhoto(Mapper.multipartFileToByteArray(dto.getPlayerPhoto()));
            }

            updatedPlayer = playerRepository.saveAndFlush(existingPlayer);
        } catch (EntityNotFoundException ex) {
            log.error("Update exception error");
            throw ex;
        }
        return updatedPlayer;
    }


    /**
     * Deletes a player based on the specified id.
     * Also deletes all the games involving the player.
     *
     * @param id the id of the player to delete.
     */
    @Transactional
    @Override
    public void deletePlayer(Long id) throws EntityNotFoundException {
        Player player;
        try {
            player = playerRepository.findPlayerByPlayerId(id);
            if (player == null) throw new EntityNotFoundException(Player.class, id);
            gameResultRepository.deleteByPlayer1OrPlayer2(player, player);
            playerRepository.delete(player);

        }catch (EntityNotFoundException ex) {
            log.error("Error in deleting player");
            throw ex;
        }
    }

    /**
     * Retrieves and calculates statistical information for a player based on their id.
     *
     * @param playerId The id of the player for whom statistics are retrieved.
     * @return A data transfer object containing statistical information for the player.
     */
    @Override
    public PlayerStatsDTO getPlayerStats(Long playerId) throws EntityNotFoundException {
        Player player;
        PlayerStatsDTO playerStatsDTO = new PlayerStatsDTO();
        try {
            player = playerRepository.findPlayerByPlayerId(playerId);
            if (player == null) {
                throw new EntityNotFoundException(Player.class, playerId);
            }
            List<GameResult> gamesPlayed = gameResultRepository.findByPlayer1_PlayerIdOrPlayer2_PlayerId(playerId, playerId);
            List<GameResult> wins = gameResultRepository.findByWinnerPlayer_PlayerId(playerId);
            List<GameResult> loses = gameResultRepository.findByLoserPlayer_PlayerId(playerId);

            playerStatsDTO.setPlayerId(playerId);
            playerStatsDTO.setTotalGames(gamesPlayed.size());
            playerStatsDTO.setTotalWins(wins.size());
            playerStatsDTO.setTotalLoses(loses.size());
            playerStatsDTO.setTotalTies(playerStatsDTO.getTotalGames()-playerStatsDTO.getTotalWins()-playerStatsDTO.getTotalLoses());
            double winPercentage = (playerStatsDTO.getTotalWins() == 0) ? 0 : ((double) playerStatsDTO.getTotalWins() / playerStatsDTO.getTotalGames()) * 100;
            playerStatsDTO.setWinPercentage(winPercentage);

            //TODO  NA VALW ISWS IN THE DTOS TA PEDIA GIA GOALS! (SUMOLIKA KAI HOME/AWAY?)

        }catch (EntityNotFoundException ex) {
            log.error("Error in finding stats");
            throw ex;
        }

        return playerStatsDTO;
    }

    /**
     * Retrieves stats and calculates player rankings based on their performance in games.
     *
     * @return A list of data transfer objects containing player rankings.
     */
    @Override
    public List<PlayerRankingsDTO> getPlayerRankings() throws EntitiesNotFoundException {
        List<Player> players;
        List<PlayerRankingsDTO> playerRankingsDTOs = new ArrayList<>();

        try {
            players = playerRepository.findAll();
            if (players.isEmpty()) throw new EntitiesNotFoundException(Player.class);


            for (Player player : players) {
                List<GameResult> gamesPlayed = gameResultRepository.findByPlayer1_PlayerIdOrPlayer2_PlayerId(player.getPlayerId(), player.getPlayerId());
                List<GameResult> wins = gameResultRepository.findByWinnerPlayer_PlayerId(player.getPlayerId());
                List<GameResult> loses = gameResultRepository.findByLoserPlayer_PlayerId(player.getPlayerId());

                int totalWins = wins.size();
                int totalTies = gamesPlayed.size()-wins.size()-loses.size();

                int totalPoints = (totalWins * 3) + totalTies;

                PlayerRankingsDTO playerRankingDTO = new PlayerRankingsDTO();
                playerRankingDTO.setPlayerId(player.getPlayerId());
                playerRankingDTO.setPlayerName(player.getPlayerName());
                playerRankingDTO.setPoints(totalPoints);

                playerRankingsDTOs.add(playerRankingDTO);
            }

            playerRankingsDTOs.sort(Comparator.comparingInt(PlayerRankingsDTO::getPoints).reversed());

            for (int i = 0; i < playerRankingsDTOs.size(); i++) {
                playerRankingsDTOs.get(i).setRanking(i + 1);
            }

            return playerRankingsDTOs;


        } catch (EntitiesNotFoundException ex) {
            log.error("Error in finding ranking");
            throw ex;
        }
    }

}
