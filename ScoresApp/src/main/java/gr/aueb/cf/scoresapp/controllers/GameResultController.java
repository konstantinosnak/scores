package gr.aueb.cf.scoresapp.controllers;

import gr.aueb.cf.scoresapp.dto.GameResultInsertDTO;
import gr.aueb.cf.scoresapp.dto.GameResultReadOnlyDTO;
import gr.aueb.cf.scoresapp.dto.GameResultUpdateDTO;
import gr.aueb.cf.scoresapp.mapper.Mapper;
import gr.aueb.cf.scoresapp.model.GameResult;
import gr.aueb.cf.scoresapp.model.Player;
import gr.aueb.cf.scoresapp.model.Team;
import gr.aueb.cf.scoresapp.service.GameResultServiceImpl;
import gr.aueb.cf.scoresapp.service.PlayerServiceImpl;
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
@RequestMapping("/results")
public class GameResultController {
    private final PlayerServiceImpl playerService;
    private final TeamServiceImpl teamService;
    private final GameResultServiceImpl gameResultService;

    @Autowired
    public GameResultController(PlayerServiceImpl playerService, TeamServiceImpl teamService, GameResultServiceImpl gameResultService) {
        this.playerService = playerService;
        this.teamService = teamService;
        this.gameResultService = gameResultService;
    }

    /**
     * Retrieves all game results and populates the model with game result data for Thymeleaf rendering.
     * Converts GameResult entities to GameResultReadOnlyDTOs for presentation in the view.
     *
     * @param model
     * @return The Thymeleaf template for displaying the list of game results.
     */
    @GetMapping
    public String getAllResults(Model model) {
        try {
            List<GameResult> gameResults = gameResultService.getAllGameResults();

            List<GameResultReadOnlyDTO> readonlyResults = gameResults.stream()
                    .map(gameResult -> new GameResultReadOnlyDTO(gameResult.getResultId(),gameResult.getGameDate(),gameResult.getPlayer1(),
                            gameResult.getPlayer2(),gameResult.getTeam1(),gameResult.getTeam2(),gameResult.getScorePlayer1(),
                            gameResult.getScorePlayer2(),gameResult.getWinnerPlayer(),gameResult.getLoserPlayer(),gameResult.getWinningTeam(),
                            gameResult.getWinningTeam(),gameResult.getCreatedBy(),gameResult.getLastModifiedBy()))
                    .collect(Collectors.toList());
            model.addAttribute("results", readonlyResults);
            return "gameResults/results";

        } catch (EntitiesNotFoundException ex) {
            return "errors/errorResultEmpty";
        }
    }

    /**
     * Prepares the form for adding a new game result by populating the model with the necessary data.
     * Retrieves a list of all players and teams.
     *
     * @param model
     * @return The Thymeleaf template for displaying the game result insertion form.
     */
    @GetMapping("/add")
    public String insertResultForm(Model model) {
        try {
            List<Player> players = playerService.getAllPlayers();
            List<Team> teams = teamService.getAllTeams();

            model.addAttribute("players", players);
            model.addAttribute("teams", teams);
            model.addAttribute("gameResultInsertDTO", new GameResultInsertDTO());

            return "gameResults/addResult";
        } catch (EntitiesNotFoundException ex) {
            return "errors/errorNotFound";
        }
    }

    /**
     * Handles the submission of the game result insertion form, inserting a new game result with the provided details.
     * Performs data binding and validation, logging any binding errors.
     *
     * @param gameResultInsertDTO The data submitted to the insertion form.
     * @param bindingResult  The result of the data binding and validation.
     * @return The Thymeleaf template based on the result of form validation and game result insertion.
     */
    @PostMapping("/add")
    public String insertResult(@ModelAttribute("gameResultInsertDTO") @Valid GameResultInsertDTO gameResultInsertDTO,
                               BindingResult bindingResult )  {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                log.error("Binding Error: " + error);
            });
        }
        try {
            gameResultService.insertGameResult(gameResultInsertDTO);
            return "redirect:/results";
        } catch (Exception ex) {
            return "errors/errorNotInsert";
        }
    }

    /**
     * Displays a form to select a game result.
     *
     * @param model
     * @return The Thymeleaf template for displaying the game result selection form.
     */
    @GetMapping("/select")
    public String selectResult(Model model) {
        try {
            List<GameResult> gameResults = gameResultService.getAllGameResults();
            model.addAttribute("results", gameResults);
            model.addAttribute("selectedGameResult", new GameResult());
            return "gameResults/selectResult";
        } catch (EntitiesNotFoundException ex) {
            return "errors/errorNotFound";
        }
    }

    @PostMapping("/selected")
    public String viewSelectedResult(@ModelAttribute("selectedGameResult") GameResult selectedGameResult, Model model) {
        try {
            Long resultId = selectedGameResult.getResultId();
            return "redirect:/results/selected/" + resultId;
        } catch (Exception ex) {
            return "errors/errorNotFound";
        }
    }

    /**
     * Displays the details of a selected game result identified by its unique identifier.
     *
     * @param resultId The unique id of the selected game result.
     * @param model
     * @return The Thymeleaf template for displaying the details of the selected game result.
     */
    @GetMapping("/selected/{resultId}")
    public String viewSelectedResult(@PathVariable Long resultId, Model model) {
        try {
            GameResult gameResult = gameResultService.getGameResultById(resultId);
            GameResultReadOnlyDTO gameResultReadOnlyDTO = new GameResultReadOnlyDTO(gameResult.getResultId(),gameResult.getGameDate(),
                    gameResult.getPlayer1(),gameResult.getPlayer2(),gameResult.getTeam1(),gameResult.getTeam2(),gameResult.getScorePlayer1(),
                    gameResult.getScorePlayer2(),gameResult.getWinnerPlayer(),gameResult.getLoserPlayer(),gameResult.getWinningTeam(),
                    gameResult.getLosingTeam(),gameResult.getCreatedBy(),gameResult.getLastModifiedBy());
            model.addAttribute("selectedGameResult", gameResultReadOnlyDTO);
            return "gameResults/selectedResult";
        } catch (EntityNotFoundException ex) {
            return "errors/errorNotFound";
        }
    }


    /**
     * Displays the form for updating a game result, pre-populated with the details of the selected game result.
     *
     * @param resultId The unique id of the game result to be updated.
     * @param model
     * @return The Thymeleaf template for the game result update form.
     */
    @GetMapping("/update/{resultId}")
    public String updateResultForm(@PathVariable Long resultId, Model model) {
        try {
            List<Player> players = playerService.getAllPlayers();
            List<Team> teams = teamService.getAllTeams();
            GameResult gameResult = gameResultService.getGameResultById(resultId);

            model.addAttribute("players", players);
            model.addAttribute("teams", teams);

            GameResultUpdateDTO resultUpdateDTO = Mapper.getGameResultUpdateDTO(gameResult); //για βοήθεια στον admin στο τι κανει update!
            model.addAttribute("resultUpdateDTO", resultUpdateDTO);

            return "gameResults/updateResult";
        } catch (EntityNotFoundException | EntitiesNotFoundException ex) {
            return "errors/errorNotFound";
        }
    }


    /**
     * Handles the submission of the game result update form, updating the game result with the provided details.
     *
     * @param resultId      The unique identifier of the game result to be updated.
     * @param resultUpdateDTO The data submitted to the update form.
     * @param bindingResult The result of the data binding and validation.
     * @return The Thymeleaf template or redirect URL based on the success or failure of the update.
     */
    @PostMapping("/update/{resultId}")
    public String updateResult(@PathVariable Long resultId, @ModelAttribute("resultUpdateDTO") @Valid GameResultUpdateDTO resultUpdateDTO,
             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                log.error("Binding Error: " + error);
            });
        }
        log.error("Received DTO: " + resultUpdateDTO);
        try {
            gameResultService.updateGameResult(resultUpdateDTO);
            return "redirect:/results/selected/" + resultId;
        } catch (EntityNotFoundException ex) {
            return "errors/errorNotFound";
        }
    }

    /**
     * Handles the deletion of a game result with the specified unique id.
     *
     * @param resultId The unique id of the game result to be deleted.
     * @return The Thymeleaf template or redirect URL based on the success or failure of the deletion.
     */
    @PostMapping("/delete/{resultId}")
    public String deleteResult(@PathVariable Long resultId) {
        try {
            gameResultService.deleteGameResult(resultId);
            return "redirect:/results";
        }catch (EntityNotFoundException ex) {
            return "errors/errorNotFound";
        }
    }
}



