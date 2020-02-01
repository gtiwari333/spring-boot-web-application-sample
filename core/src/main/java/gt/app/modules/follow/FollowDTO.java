package gt.app.modules.follow;

import lombok.Data;

@Data
public class FollowDTO {
    /*
    this DTO can be used in both purpose.. to store my follower as well as whom i'm following
     */

    private String uniqueId;

    private String firstName;
    private String lastName;

    public FollowDTO(String uniqueId, String firstName, String lastName) {
        this.uniqueId = uniqueId;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
