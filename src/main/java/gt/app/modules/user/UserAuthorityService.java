package gt.app.modules.user;

import gt.app.config.security.AppUserDetails;
import gt.app.domain.Note;
import gt.app.domain.User;
import gt.app.modules.note.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("appSecurity")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserAuthorityService {

    private final NoteService noteService;

    public boolean hasAccess(AppUserDetails curUser, Long id, String entity) {

        if (curUser.isSystemAdmin()) {
            return true;
        }

        if (User.class.getSimpleName().equalsIgnoreCase(entity)) {
            return id.equals(curUser.getId());
        }


        if (Note.class.getSimpleName().equalsIgnoreCase(entity)) {

            Long createdById = noteService.findCreatedByUserIdById(id);

            return createdById.equals(curUser.getId());
        }


        /*
        add more rules
         */

        return false;
    }

}
