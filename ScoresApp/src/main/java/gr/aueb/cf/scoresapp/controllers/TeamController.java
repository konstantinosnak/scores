package gr.aueb.cf.scoresapp.controllers;

import gr.aueb.cf.scoresapp.dto.*;
import gr.aueb.cf.scoresapp.model.Team;
import gr.aueb.cf.scoresapp.service.GameResultServiceImpl;
import gr.aueb.cf.scoresapp.service.TeamServiceImpl;
import gr.aueb.cf.scoresapp.service.exceptions.EntitiesNotFoundException;
import gr.aueb.cf.scoresapp.service.exceptions.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/teams")
public class TeamController {
    private final TeamServiceImpl teamService;
    private final GameResultServiceImpl gameResultService;

    @Autowired
    public TeamController(TeamServiceImpl teamService, GameResultServiceImpl gameResultService) {
        this.teamService = teamService;
        this.gameResultService = gameResultService;
    }

    /**
     * Retrieves all teams and populates the model with team data for Thymeleaf rendering.
     * Converts Team entities to TeamReadOnlyDTOs for presentation in the view.
     *
     * @param model
     * @return The Thymeleaf template for displaying the list of teams.
     */
    @GetMapping
    public String getAllTeams(Model model) {
        try {
            List<Team> teams = teamService.getAllTeams();
            List<TeamReadOnlyDTO> teamDTOs = teams.stream()
                    .map(team -> new TeamReadOnlyDTO(team.getTeamId(),team.getTeamName() ,team.getPhotoBase64(), team.getSummary()))
                    .collect(Collectors.toList());
            model.addAttribute("teams", teamDTOs);
            return "teams/teams";

        }catch (EntitiesNotFoundException ex) {
            return "errors/errorTeamEmpty";
        }
    }

    /**
     * Prepares the form for adding a new team by creating and adding an empty TeamInsertDTO to the model.
     *
     * @param model
     * @return The Thymeleaf template for the team insertion form.
     */
    @GetMapping("/add")
    public String insertTeamForm(Model model) {
        model.addAttribute("teamInsertDTO", new TeamInsertDTO());
        return "/teams/addTeam";
    }

    /**
     * Handles the submission of the team addition form, validates the input, and inserts a new team.
     *
     * @param teamInsertDTO he DTO containing the information for the new team.
     * @param bindingResult The result of the data binding and validation.
     * @return The Thymeleaf template based on the result of form validation and team insertion.
     */
    @PostMapping("/add")
    public String insertTeam(@ModelAttribute("teamInsertDTO")@Valid TeamInsertDTO teamInsertDTO,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/teams/addTeam";
        }
        try {
            teamService.insertTeam(teamInsertDTO);
            return "redirect:/teams";
        }catch (Exception ex) {
            log.error("Error in inserting new player");
            return "errors/errorNotInsert";
        }
    }

    /**
     * Displays a form to select a player.
     *
     * @param model
     * @return The Thymeleaf template for displaying the team selection form.
     */
    @GetMapping("/select")
    public String selectTeam(Model model) {
        try {
            List<Team> teams = teamService.getAllTeams();
            List<TeamReadOnlyDTO> teamDTOs = teams.stream()
                    .map(team -> new TeamReadOnlyDTO(team.getTeamId(), team.getTeamName(), team.getPhotoBase64(), team.getSummary()))
                    .collect(Collectors.toList());
            model.addAttribute("teams", teamDTOs);
            model.addAttribute("selectedTeam", new TeamReadOnlyDTO());
            return "/teams/selectTeam";
        } catch (EntitiesNotFoundException ex) {
            return "errors/errorNotFound";
        }
    }

    @PostMapping("/selected")
    public String viewSelectedTeam(@ModelAttribute("selectedTeam") Team selectedTeam) {
        try {
            Long teamId = selectedTeam.getTeamId();
            return "redirect:/teams/selected/" + teamId;
        }catch (Exception ex) {
            return "errors/errorNotFound";
        }
    }

    /**
     * Displays information about a selected team by retrieving its details, including photo and summary.
     *
     * @param teamId The unique id of the selected team.
     * @param model
     * @return The Thymeleaf template for displaying the details of the selected team.
     */
    @GetMapping("/selected/{teamId}")
    public String viewSelectedTeam(@PathVariable Long teamId, Model model) {
        try {
            Team team = teamService.getTeamWithPhotoById(teamId);
            TeamReadOnlyDTO selectedTeam = new TeamReadOnlyDTO(team.getTeamId(),team.getTeamName(),team.getPhotoBase64(), team.getSummary());

            model.addAttribute("selectedTeam", selectedTeam);
            return "teams/selectedTeam";
        } catch (EntityNotFoundException ex) {
            return "errors/errorNotFound";
        }
    }

    /**
     * Prepares the form for updating a team.
     *
     * @param teamId The unique id of the team to be updated.
     * @param model
     * @return The Thymeleaf template for the team update form.
     */
    @GetMapping("/update/{teamId}")
    public String updateTeamForm(@PathVariable Long teamId, Model model) {
        try {
            TeamUpdateDTO teamUpdateDTO = new TeamUpdateDTO();
            Team team = teamService.getTeamById(teamId);
            teamUpdateDTO.setTeamId(team.getTeamId());
            model.addAttribute("teamUpdateDTO", teamUpdateDTO);
            return "teams/updateTeam";
        }catch (EntityNotFoundException ex) {
            return "errors/errorNotFound";
        }
    }

    /**
     * Handles the submission of the team update form, updating the team with the provided details.
     *
     * @param teamId        The unique id of the team to be updated.
     * @param teamUpdateDTO The data submitted to the update form.
     * @param bindingResult The result of the data binding, containing potential validation errors.
     * @return The Thymeleaf template or redirect URL based on the success or failure of the update.
     */
    @PostMapping("/update/{teamId}")
    public String updateTeam(@PathVariable Long teamId,@ModelAttribute("teamUpdateDTO") @Valid TeamUpdateDTO teamUpdateDTO,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/teams/updateTeam";
        }
        try {
            teamService.updateTeam(teamUpdateDTO);
            return "redirect:/teams/selected/" + teamId;
        }catch (EntityNotFoundException ex) {
            return "errors/errorNotFound";
        }
    }

    /**
     * Handles the deletion of a team identified by the provided unique id.
     *
     * @param teamId The unique id of the team to be deleted.
     * @return Redirects to /teams or to error page based on the success or failure of the deletion.
     */
    @PostMapping("/delete/{teamId}")
    public String deleteTeam(@PathVariable Long teamId){
        try {
            teamService.deleteTeam(teamId);
            return "redirect:/teams";
        }catch (EntityNotFoundException ex) {
            return "errors/errorNotFound";
        }
    }

}
