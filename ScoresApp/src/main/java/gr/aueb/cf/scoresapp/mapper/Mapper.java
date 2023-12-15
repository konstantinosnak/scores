package gr.aueb.cf.scoresapp.mapper;

import gr.aueb.cf.scoresapp.dto.*;
import gr.aueb.cf.scoresapp.model.GameResult;
import gr.aueb.cf.scoresapp.model.Player;
import gr.aueb.cf.scoresapp.model.Team;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@Slf4j
public class Mapper {

    /**
     * Converts a MultipartFile to a byte array.
     *
     * @param file The MultipartFile to be converted.
     * @return A byte array representing the content of the MultipartFile, or null if the file is null.
     */
    public static byte[] multipartFileToByteArray(MultipartFile file) {
        try {
            return (file != null) ? file.getBytes() : null;
        } catch (IOException ex) {
            log.error("Error converting MultipartFile to byte array", ex);
            return null;
        }
    }


    public static GameResultUpdateDTO getGameResultUpdateDTO(GameResult gameResult) {
        GameResultUpdateDTO resultUpdateDTO = new GameResultUpdateDTO();
        resultUpdateDTO.setGameResultId(gameResult.getResultId());
        resultUpdateDTO.setGameDate(gameResult.getGameDate());
        resultUpdateDTO.setPlayer1Id(gameResult.getPlayer1().getPlayerId());
        resultUpdateDTO.setPlayer2Id(gameResult.getPlayer2().getPlayerId());
        resultUpdateDTO.setTeam1Id(gameResult.getTeam1().getTeamId());
        resultUpdateDTO.setTeam2Id(gameResult.getTeam2().getTeamId());
        resultUpdateDTO.setScorePlayer1(gameResult.getScorePlayer1());
        resultUpdateDTO.setScorePlayer2(gameResult.getScorePlayer2());
        return resultUpdateDTO;
    }

}
