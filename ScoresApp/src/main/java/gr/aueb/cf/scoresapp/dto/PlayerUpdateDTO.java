package gr.aueb.cf.scoresapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerUpdateDTO{
    private Long playerId;
    @NotBlank(message = "Player name is required")
    @Size(min = 3, max = 30, message = "Player name must be between 3 and 30 characters!")
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "Player name must not contain special characters")
    private String playerName;
    @Nullable
    private MultipartFile playerPhoto;
}
