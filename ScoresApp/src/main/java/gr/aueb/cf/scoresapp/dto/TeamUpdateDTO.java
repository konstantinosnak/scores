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
public class TeamUpdateDTO{

    private Long teamId;

    @NotBlank(message = "Team name is required")
    @Size(min = 3, max = 30, message = "Team name must be between 3 and 30 characters!")
    //επιτρέπονται αριθμοί καθώς στην Γερμανία χρησιμοποιούν νούμερα στα ονόματα (πχ Schalke 04 ή  FSV Mainz 05)
    @Pattern(regexp = "^[a-zA-Z0-9. ]*$", message = "Team name must not contain special characters except for dots (.)")
    private String teamName;

    private MultipartFile teamPhoto;

    @Size(max = 1500, message = "Team summary must not be over 1500 chars.")
    private String summary;
}
