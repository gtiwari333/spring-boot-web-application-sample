package gt.app.web.rest;

import gt.app.config.security.SecurityUtils;
import gt.app.modules.bookmark.BookmarkService;
import gt.app.modules.bookmark.BookmarkWithArticleDTO;
import gt.common.utils.HeaderUtil;
import gt.common.utils.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/bookmark")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping("/is-bookmarked/{articleId}")
    public ResponseEntity<Boolean> isFollowing(@PathVariable @NotNull Long articleId) {
        return ok(bookmarkService.isBookmarked(SecurityUtils.getCurrentUserId(), articleId));
    }

    @GetMapping({"/", ""})
    public ResponseEntity<List<BookmarkWithArticleDTO>> getAllBookmarks(@PageableDefault(value = 30) Pageable pageable) {
        Page<BookmarkWithArticleDTO> page = bookmarkService.findAllBySubscriberId(SecurityUtils.getCurrentUserId(), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/subscription/bookmark");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @PutMapping("{articleId}")
    public ResponseEntity<Void> addBookmark(@PathVariable @NotNull Long articleId) {
        bookmarkService.add(SecurityUtils.getCurrentUserId(), articleId);
        return ResponseEntity.ok().headers(HeaderUtil.alertMsg("Bookmark Added")).build();
    }

    @DeleteMapping("{articleId}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable @NotNull Long articleId) {
        bookmarkService.delete(SecurityUtils.getCurrentUserId(), articleId);
        return ResponseEntity.ok().headers(HeaderUtil.deleted("Bookmark")).build();
    }


}
