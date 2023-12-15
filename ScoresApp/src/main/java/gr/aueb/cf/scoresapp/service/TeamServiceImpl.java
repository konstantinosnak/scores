package gr.aueb.cf.scoresapp.service;

import gr.aueb.cf.scoresapp.dto.TeamInsertDTO;
import gr.aueb.cf.scoresapp.dto.TeamUpdateDTO;
import gr.aueb.cf.scoresapp.mapper.Mapper;
import gr.aueb.cf.scoresapp.model.Player;
import gr.aueb.cf.scoresapp.model.Team;
import gr.aueb.cf.scoresapp.repository.GameResultRepository;
import gr.aueb.cf.scoresapp.repository.TeamRepository;
import gr.aueb.cf.scoresapp.service.exceptions.EntitiesNotFoundException;
import gr.aueb.cf.scoresapp.service.exceptions.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class TeamServiceImpl implements ITeamService{
    private final TeamRepository teamRepository;
    private final GameResultRepository gameResultRepository;

    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository, GameResultRepository gameResultRepository) {
        this.teamRepository = teamRepository;
        this.gameResultRepository = gameResultRepository;
    }

    /**
     *  Retrieves a list of all teams.
     *
     * @return A list containing all team entities.
     */
    @Override
    public List<Team> getAllTeams() throws EntitiesNotFoundException {
        List<Team> teams;
        try {
            teams = teamRepository.findAll();
            if (teams.isEmpty()) throw new EntitiesNotFoundException(Team.class);
            for (Team team : teams) {
                if (team.getTeamPhoto() != null) {
                    team.setPhotoBase64(Base64.getEncoder().encodeToString(team.getTeamPhoto()));
                }
            }

        }catch (EntitiesNotFoundException ex) {
            log.error("Error in finding teams");
            throw ex;
        }
        return teams;
    }

    /**
     * Retrieves a team by their id.
     *
     * @param id the id of the team
     * @return The team entity associated with the id
     */
    @Override
    public Team getTeamById(Long id) throws EntityNotFoundException {
        Team team = teamRepository.findTeamByTeamId(id);
        if (team == null) {
            throw new EntityNotFoundException(Team.class, id);
        }
        return team;
    }

    /**
     * Retrieves a team by its id along with the team's photo in Base64 format.
     *
     * @param teamId id of team
     * @return The team entity with the associated photo in the Base64 format.
     */
    @Override
    public Team getTeamWithPhotoById(Long teamId) throws EntityNotFoundException {
        Team team = teamRepository.findTeamByTeamId(teamId);
        if (team == null) {
            throw new EntityNotFoundException(Team.class, teamId);
        }
        team.setPhotoBase64(Base64.getEncoder().encodeToString(team.getTeamPhoto()));
        return team;
    }

    /**
     * Inserts a new team based on the provided TeamInsertDTO.
     *
     * @param dto The data transfer object containing information for creating the new team.
     * @return The newly created team entity.
     */
    @Transactional
    @Override
    public Team insertTeam(TeamInsertDTO dto) throws Exception {
        Team team = new Team();
        try {
            team.setTeamName(dto.getTeamName());
            team.setTeamPhoto(Mapper.multipartFileToByteArray(dto.getTeamPhoto()));
            team.setSummary(dto.getSummary());
            team = teamRepository.saveAndFlush(team);
            if (team.getTeamId() == null) throw new Exception("Insert Error Exception");
        } catch (Exception ex) {
            log.error("Error in inserting Team");
            throw ex;
        }
        return team;
    }

    /**
     * Updates an existing team in the system based on the provided TeamUpdateDTO.
     *
     * @param dto The data transfer object containing information for updating the team.
     * @return The updated team entity.
     */
    @Transactional
    @Override
    public Team updateTeam(TeamUpdateDTO dto) throws EntityNotFoundException {
        Team updatedTeam;
        Team existingTeam;
        try {
            existingTeam = teamRepository.findTeamByTeamId(dto.getTeamId());
            if (existingTeam == null) throw new EntityNotFoundException(Team.class, dto.getTeamId());

            existingTeam.setTeamName(dto.getTeamName());
            if (dto.getTeamPhoto() != null && dto.getTeamPhoto().getSize() != 0) {
                existingTeam.setTeamPhoto(Mapper.multipartFileToByteArray(dto.getTeamPhoto()));
            }

            if (dto.getSummary() != null && !dto.getSummary().isEmpty()) {
                existingTeam.setSummary(dto.getSummary());
            }

            updatedTeam = teamRepository.saveAndFlush(existingTeam);
        } catch (EntityNotFoundException ex) {
            log.error("Error in updating Team");
            throw ex;
        }
        return updatedTeam;
    }

    /**
     * Deletes a team based on the specified id.
     * Also deletes all the games involving the team.
     *
     * @param id the id of the team to delete.
     */
    @Transactional
    @Override
    public Team deleteTeam(Long id) throws EntityNotFoundException {
        Team team;
        try {
            team = teamRepository.findTeamByTeamId(id);
            if (team == null) throw new EntityNotFoundException(Team.class, id);
            gameResultRepository.deleteByTeam1OrTeam2(team, team);
            teamRepository.delete(team);

        }catch (EntityNotFoundException ex) {
            log.error("Error in deleting team");
            throw ex;
        }
        return team;
    }
}
