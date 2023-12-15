package oxahex.asker.domain.notification;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import oxahex.asker.domain.user.User;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

  @Query("select n from Notification as n where n.receiveUser = :user and n.readDateTime = null")
  Slice<Notification> findAllUnReadNotification(@Param("user") User user, PageRequest pageRequest);
}
