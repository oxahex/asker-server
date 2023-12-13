package oxahex.asker.domain.answer;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import oxahex.asker.domain.user.User;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

  @Query("select a from Answer as a where a.answerUser = :user order by a.createdDateTime desc")
  List<Answer> findAllByAnswerUserDesc(@Param("user") User user);

  @Query("select a from Answer as a where a.answerUser = :user order by a.createdDateTime asc")
  List<Answer> findAllByAnswerUserASC(@Param("user") User user);
}
