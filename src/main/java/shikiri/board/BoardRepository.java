package shikiri.board;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import org.springframework.data.domain.Sort;

@Repository
public interface BoardRepository extends CrudRepository<BoardModel, String> {
    // Find all boards
    @SuppressWarnings("null")

    // Find all boards by user ID - Can be empty
    Optional<List<BoardModel>> findAllByUserId(String userId);

    // Find a board by ID and user ID - Can be Empty
    Optional<BoardModel> findByIdAndUserId(String id, String userId);

    // Find all boards by user ID and name containing - Can be empty
    Optional<List<BoardModel>> findByNameContainingAndUserId(String name, Sort sort, String userId);

    // Find all boards by user ID ordered by name - Can be empty
    Optional<List<BoardModel>> findByUserIdOrderByNameDesc(String userId);
}