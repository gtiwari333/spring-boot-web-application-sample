package gt.app.web.rest;

import gt.app.domain.Topic;
import gt.app.exception.RecordNotFoundException;
import gt.app.modules.topic.TopicDTO;
import gt.app.modules.topic.TopicService;
import gt.common.utils.HeaderUtil;
import gt.common.utils.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/topic")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @GetMapping("/{id}")
    public ResponseEntity<Topic> get(@PathVariable @NotNull Long id) {
        return topicService.findById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new RecordNotFoundException("Topic", "id", id));
    }

    @GetMapping
    public ResponseEntity<List<Topic>> getAll(@PageableDefault(value = 30) Pageable pageable) {
        Page<Topic> page = topicService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/topic");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> create(@RequestBody @NotNull TopicDTO toCreate) throws URISyntaxException {

        if (toCreate.getId() != null) {
            return ResponseEntity.badRequest().body("Id should be null");
        }

        if (topicService.existsByName(toCreate.getName())) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.failureMsg("Topic with name " + toCreate.getName() + " already exists"))
                .build();
        }

        Topic topic = new Topic();
        BeanUtils.copyProperties(toCreate, topic);

        topicService.save(topic);
        return ResponseEntity.created(new URI("/api/topic/" + topic.getId()))
            .headers(HeaderUtil.created("Topic"))
            .body(topic);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> update(@RequestBody @NotNull TopicDTO toUpdate) throws URISyntaxException {

        if (toUpdate.getId() == null) {
            return ResponseEntity.badRequest().body("Id should't be null");
        }

        if (topicService.existsByNameAndIdNot(toUpdate.getName(), toUpdate.getId())) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.failureMsg("Topic with name " + toUpdate.getName() + " already exists"))
                .build();
        }

        Topic topic = new Topic();
        BeanUtils.copyProperties(toUpdate, topic);

        topicService.save(topic);

        return ResponseEntity.created(new URI("/api/topic/" + topic.getId()))
            .headers(HeaderUtil.updated("Topic"))
            .body(topic);
    }


}
