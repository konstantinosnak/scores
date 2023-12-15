package gr.aueb.cf.scoresapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamReadOnlyDTO{
    private Long teamId;
    private String teamName;
    private String teamPhotoBase64;
    private String summary;
}
