package gr.aueb.cf.scoresapp.controllers;

import gr.aueb.cf.scoresapp.dto.*;
import gr.aueb.cf.scoresapp.model.GameResult;
import gr.aueb.cf.scoresapp.model.Player;
import gr.aueb.cf.scoresapp.service.GameResultServiceImpl;
import gr.aueb.cf.scoresapp.service.PlayerServiceImpl;
import gr.aueb.cf.scoresapp.service.exceptions.EntitiesNotFoundException;
import gr.aueb.cf.scoresapp.service.exceptions.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/players")
public class PlayerController {
    private final PlayerServiceImpl playerService;
    private final GameResultServiceImpl gameResultService;

    @Autowired
    public PlayerController(PlayerServiceImpl playerService, GameResultServiceImpl gameResultService) {
        this.playerService = playerService;
        this.gameResultService = gameResultService;
    }

    /**
     * Retrieves all players and populates the model with player data for Thymeleaf rendering.
     * Converts Player entities to PlayerReadOnlyDTOs for presentation in the view.
     *
     * @param model used to store and share data between the controller and the Thymeleaf view.
     * @return The Thymeleaf template that will be used to display the list of players.
     */
    @GetMapping
    public String getAllPlayers(Model model) {
        try {
            List<Player> players = playerService.getAllPlayers();
            List<PlayerReadOnlyDTO> playerDTOs = players.stream()
                    .map(player -> new PlayerReadOnlyDTO(player.getPlayerId(), player.getPlayerName(), player.getPhotoBase64()))
                    .collect(Collectors.toList());
            model.addAttribute("players", playerDTOs);
            return "/players/players";
        } catch (EntitiesNotFoundException ex) {
            log.error("Error retrieving players: {}", ex.getMessage());
            model.addAttribute("error", "Error!");
            return "errors/errorPlayerEmpty";
        }
    }

    /**
     * Prepares the form for adding a new player by creating and adding an empty PlayerInsertDTO to the model.
     *
     * @param model
     * @return The Thymeleaf template for displaying the player addition form.
     */
    @GetMapping("/add")
    public String insertPlayerForm(Model model) {
        model.addAttribute("playerInsertDTO", new PlayerInsertDTO());
        return"/players/addPlayer";
    }

    /**
     * Handles the submission of the player addition form, validates the input, and inserts a new player.
     *
     * @param playerInsertDTO The DTO containing the information for the new player.
     * @param bindingResult The result of the data binding and validation.
     * @return If validation fails, returns the Thymeleaf template for displaying
     *         the player addition form with errors.
     *         If insertion is successful, redirects to the /players. If an error occurs during insertion,
     *         shows an error page.
     */
    @PostMapping("/add")
    public String insertPlayer(@ModelAttribute("playerInsertDTO") @Valid PlayerInsertDTO playerInsertDTO,
                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/players/addPlayer";
        }
        try {
            playerService.insertPlayer(playerInsertDTO);
            return "redirect:/players";
        }catch (Exception ex) {
            log.error("Error in inserting new player!");
            return "errors/errorNotInsert";
        }
    }


    /**
     * Displays a form to select a player.
     *
     * @param model
     * @return The Thymeleaf template for displaying the player selection form.
     */
    @GetMapping("/select")
    public String selectPlayer(Model model) {
        try {
            List<Player> players = playerService.getAllPlayers();
            List<PlayerReadOnlyDTO> playerDTOs = players.stream()
                    .map(player -> new PlayerReadOnlyDTO(player.getPlayerId(), player.getPlayerName(), player.getPhotoBase64()))
                    .collect(Collectors.toList());
            model.addAttribute("players", playerDTOs);
            model.addAttribute("selectedPlayer", new PlayerReadOnlyDTO());
            return "players/selectPlayer";
        } catch (EntitiesNotFoundException ex) {
            return "errors/errorNotFound";
        }
    }


    @PostMapping("/selected")
    public String viewSelectedPlayer(@ModelAttribute("selectedPlayer") Player selectedPlayer) {
        try {
            Long playerId = selectedPlayer.getPlayerId();
            return "redirect:/players/selected/" + playerId;
        } catch (Exception ex) {
            return "errors/errorNotFound";
        }
    }

    /**
     * Displays details for the selected player, including their photo and game results.
     *
     * @param playerId the id of the selected player
     * @param model
     * @return The Thymeleaf template for displaying details of the selected player.
     */
    @GetMapping("/selected/{playerId}")
    public String viewSelectedPlayer(@PathVariable Long playerId, Model model) {
        try {
            Player player = playerService.getPlayerWithPhotoById(playerId);
            PlayerReadOnlyDTO selectedPlayer = new PlayerReadOnlyDTO(player.getPlayerId(),player.getPlayerName(),player.getPhotoBase64());
            List<GameResult> gamesPlayed = gameResultService.countPlayerGames(player);
            List<GameResultReadOnlyDTO> readonlyGamesPlayed = gamesPlayed.stream()
                    .map(gameResult -> new GameResultReadOnlyDTO(gameResult.getResultId(),gameResult.getGameDate(),gameResult.getPlayer1(),
                            gameResult.getPlayer2(),gameResult.getTeam1(),gameResult.getTeam2(),gameResult.getScorePlayer1(),
                            gameResult.getScorePlayer2(),gameResult.getWinnerPlayer(),gameResult.getLoserPlayer(),gameResult.getWinningTeam(),
                            gameResult.getWinningTeam(),gameResult.getCreatedBy(),gameResult.getLastModifiedBy()))
                    .collect(Collectors.toList());
            model.addAttribute("selectedPlayer", selectedPlayer);
            model.addAttribute("gamesPlayed", readonlyGamesPlayed);
            return "players/selectedPlayer";
        } catch (EntityNotFoundException ex) {
            return "errors/errorNotFound";
        }
    }

    /**
     * Displays statistics for a specific player, including their photo and meta stats like their win rate.
     *
     * @param playerId the id of the player for whom statistics are being displayed.
     * @param model
     * @return The Thymeleaf template for displaying the player statistics.
     */
    @GetMapping("/stats/{playerId}")
    public String viewPlayerStats(@PathVariable Long playerId, Model model) {
        try {
            Player player = playerService.getPlayerWithPhotoById(playerId);
            PlayerStatsDTO playerStats = playerService.getPlayerStats(playerId);
            model.addAttribute("player", player);
            model.addAttribute("playerStats", playerStats);
            return "/players/playerStats";
        } catch (EntityNotFoundException ex) {
            return "errors/errorNotFound";
        }
    }

    /**
     * Displays the form for updating player information.
     *
     * @param playerId The id of the player to be updated.
     * @param model
     * @return The Thymeleaf template for the player update form.
     */
    @GetMapping("/update/{playerId}")
    public String updatePlayerForm(@PathVariable Long playerId, Model model) {
        try {
            PlayerUpdateDTO playerUpdateDTO = new PlayerUpdateDTO();
            Player player = playerService.getPlayerById(playerId);//για έλεγχο ότι υπάρχει
            playerUpdateDTO.setPlayerId(player.getPlayerId());
            model.addAttribute("playerUpdateDTO", playerUpdateDTO);
            return "players/updatePlayer";
        } catch (EntityNotFoundException ex) {
            return "errors/errorNotFound";
        }
    }

    /**
     * Handles the submission of the player update form and updates the player information.
     *
     * @param playerId The id of the player being updated.
     * @param playerUpdateDTO The data submitted in the update form.
     * @param bindingResult The result of the data binding, containing potential validation errors.
     * @return The Thymeleaf template or redirect URL based on the success or failure of the update.
     */
    @PostMapping("/update/{playerId}")
    public String updatePlayer(@PathVariable Long playerId, @ModelAttribute("playerUpdateDTO") @Valid PlayerUpdateDTO playerUpdateDTO,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "players/updatePlayer";
        }
        try {
            playerService.updatePlayer(playerUpdateDTO);
            return "redirect:/players/stats/" + playerId;
        } catch (EntityNotFoundException ex) {
            return "errors/errorNotFound";
        }
    }

    /**
     * Handles the deletion of a player by the provided unique id.
     *
     * @param playerId The id of the player to be deleted.
     * @return Redirects to /players or to error page based on the success or failure of the deletion.
     */
    @PostMapping("/delete/{playerId}")
    public String deletePlayer(@PathVariable Long playerId) {
        try {
            playerService.deletePlayer(playerId);
            return "redirect:/players";
        } catch (EntityNotFoundException ex) {
            return "errors/errorNotFound";
        }
    }

    /**
     * Displays the player rankings table.
     *
     * @param model
     * @return The Thymeleaf template for the players ranking table.
     */
    @GetMapping("/rankings")
    public String getPlayerTableScore(Model model) {
        try {
            List<PlayerRankingsDTO> playerRankingsDTOs = playerService.getPlayerRankings();
            model.addAttribute("playerRankings", playerRankingsDTOs);
            return "players/scoreTable";
        } catch (EntitiesNotFoundException ex) {
            return "errors/errorPlayerEmpty";
        }
    }

}
