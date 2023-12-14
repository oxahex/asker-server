package oxahex.asker.domain.ask;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import oxahex.asker.domain.user.User;

@Repository
public interface AskRepository extends JpaRepository<Ask, Long> {

  @Query("select a from Ask as a join Dispatch as d on a.id = d.id where d.answerUser = :user order by a.createdDateTime desc")
  List<Ask> findAllByUser(@Param("user") User user, Sort sort);
}
