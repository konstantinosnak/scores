package gr.aueb.cf.scoresapp.service;

import gr.aueb.cf.scoresapp.dto.TeamInsertDTO;
import gr.aueb.cf.scoresapp.dto.TeamUpdateDTO;
import gr.aueb.cf.scoresapp.model.Player;
import gr.aueb.cf.scoresapp.model.Team;
import gr.aueb.cf.scoresapp.service.exceptions.EntitiesNotFoundException;
import gr.aueb.cf.scoresapp.service.exceptions.EntityNotFoundException;

import java.util.List;

public interface ITeamService {
    List<Team> getAllTeams() throws EntitiesNotFoundException;
    Team getTeamById(Long id) throws EntityNotFoundException;
    Team insertTeam(TeamInsertDTO dto) throws Exception;
    Team updateTeam(TeamUpdateDTO dto) throws EntityNotFoundException;
    Team deleteTeam(Long id) throws EntityNotFoundException;
    Team getTeamWithPhotoById(Long teamId) throws EntityNotFoundException;

}
