package oxahex.asker.domain.dispatch;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import oxahex.asker.domain.ask.Ask;

@Repository
public interface DispatchRepository extends JpaRepository<Dispatch, Long> {

  Optional<Dispatch> findByAsk(Ask ask);

  List<Dispatch> findAllByAnswerUser_Id(Long answerUserId);
}
