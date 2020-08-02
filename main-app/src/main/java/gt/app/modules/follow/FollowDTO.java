package gt.app.modules.follow;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class FollowDTO {
    /*
    this DTO can be used in both purpose.. to store my follower as well as whom i'm following
     */

    private UUID id;
    private String username;

    private String firstName;
    private String lastName;
}
