package gt.app.utl;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

public class PaginationUtil {

    public static <T> void decorateModel(Model model, Page<T> page) {
        int gap = page.getNumber() + 3;
        int begin = Math.max(1, gap - 5);
        int end = Math.min(begin + 6, page.getTotalPages());
        if (page.getTotalPages() == 0) {
            begin = 0;
        }
        model.addAttribute("_page_begin", begin);
        model.addAttribute("_page_end", end);
    }
}
