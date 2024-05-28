package shikiri.board;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;

@Service
public class BoardService {

    private BoardRepository boardRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @CachePut(value = "boardCache", key = "#result.id")
    public Board create(Board boardIn, String userId) {
        boardIn.userId(userId);
        return boardRepository.save(new BoardModel(boardIn)).to();
    }

    @CachePut(value = "boardCache", key = "#id")
    public Board update(String id, Board boardIn, String userId) {
        return boardRepository.findByIdAndUserId(id, userId)
                .map(existingBoardModel -> {
                    existingBoardModel.name(boardIn.name())
                                      .description(boardIn.description());
                    return boardRepository.save(existingBoardModel).to();
                })
                .orElse(null);
    }

    @CacheEvict(value = "boardCache", key = "#id")
    public boolean delete(String id, String userId) {
        return boardRepository.findByIdAndUserId(id, userId)
                .map(board -> {
                    boardRepository.deleteById(board.id());
                    return true;
                })
                .orElse(false);
    }

    @Cacheable(value = "boardCache", key = "#userId")
    public List<Board> findAll(String userId) {
        return boardRepository.findAllByUserId(userId)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(BoardModel::to)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "boardCache", key = "#id")
    public Board findById(String id, String userId) {
        return boardRepository.findByIdAndUserId(id, userId)
                .map(BoardModel::to)
                .orElse(null);
    }

    @Cacheable(value = "boardCache", key = "#name + #sortBy + #userId")
    public List<Board> findByNameContaining(String name, String sortBy, String userId) {
        return boardRepository.findByNameContainingAndUserId(name, Sort.by(sortBy), userId)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(BoardModel::to)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "boardCache", key = "#userId")
    public List<Board> findOrderByName(String userId) {
        return boardRepository.findByUserIdOrderByNameDesc(userId)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(BoardModel::to)
                .collect(Collectors.toList());
    }
}
