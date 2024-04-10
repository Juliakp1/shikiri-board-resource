package shikiri.board;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

@Repository
public interface BoardRepository extends CrudRepository<BoardModel, String> {
    // Find all boards
    @SuppressWarnings("null")
    List<BoardModel> findAll();

    // Find all boards by a partial match of their name
    List<BoardModel> findByNameContaining(String name, Sort sort);

    // Example method to find all boards and order them, for instance by 'createdDate'
    List<BoardModel> findAllByOrderByCreatedDateDesc();
}