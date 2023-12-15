package gr.aueb.cf.scoresapp.service;

import gr.aueb.cf.scoresapp.dto.PlayerInsertDTO;
import gr.aueb.cf.scoresapp.dto.PlayerRankingsDTO;
import gr.aueb.cf.scoresapp.dto.PlayerStatsDTO;
import gr.aueb.cf.scoresapp.dto.PlayerUpdateDTO;
import gr.aueb.cf.scoresapp.model.Player;
import gr.aueb.cf.scoresapp.service.exceptions.EntitiesNotFoundException;
import gr.aueb.cf.scoresapp.service.exceptions.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

    public interface IPlayerService {

        List<Player> getAllPlayers() throws EntitiesNotFoundException;
        Player getPlayerById(Long id) throws EntityNotFoundException;
        Player insertPlayer(PlayerInsertDTO dto) throws Exception;
        Player updatePlayer(PlayerUpdateDTO dto) throws EntityNotFoundException;
        void deletePlayer(Long id) throws EntityNotFoundException;

        PlayerStatsDTO getPlayerStats(Long playerId) throws EntityNotFoundException;
        Player getPlayerWithPhotoById(Long playerId) throws EntityNotFoundException;
        List<PlayerRankingsDTO> getPlayerRankings() throws EntitiesNotFoundException;

    }
