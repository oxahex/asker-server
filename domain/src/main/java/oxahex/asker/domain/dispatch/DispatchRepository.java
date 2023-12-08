package oxahex.asker.domain.dispatch;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DispatchRepository extends JpaRepository<Dispatch, Long> {

  Optional<Dispatch> findByAsk_Id(Long askId);
}
