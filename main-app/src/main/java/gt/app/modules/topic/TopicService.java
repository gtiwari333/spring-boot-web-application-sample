package gt.app.modules.topic;

import gt.app.domain.Topic;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TopicService {

    final TopicRepository topicRepository;

    public Optional<Topic> findById(Long id) {
        return topicRepository.findById(id);
    }

    public Page<Topic> findAll(Pageable pageable) {
        return topicRepository.findAll(pageable);
    }

    public boolean existsByName(String name) {
        return topicRepository.existsByName(name);
    }

    public void save(Topic topic) {
        topicRepository.save(topic);
    }

    public boolean existsByNameAndIdNot(String name, Long id) {
        return topicRepository.existsByNameAndIdNot(name, id);
    }

    public Topic getReference(Long topicId) {
        return topicRepository.getOne(topicId);
    }
}
