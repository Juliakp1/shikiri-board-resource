package shikiri.board;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import shikiri.auth.AuthController;
import shikiri.auth.SolveIn;
import shikiri.auth.SolveOut;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BoardService {

    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);

    private BoardRepository boardRepository;
    private AuthController authController;

    @Autowired
    public BoardService(BoardRepository boardRepository, AuthController authController) {
        this.boardRepository = boardRepository;
        this.authController = authController;
    }

    private String getUserIdFromToken(String authToken) {
        SolveIn authIn = SolveIn.builder().token(authToken).build();
        ResponseEntity<SolveOut> response = authController.solve(authIn);
        if (!response.getStatusCode().is2xxSuccessful() || !response.hasBody()) {
            logger.error("Failed to authenticate token: {}", authToken);
            throw new RuntimeException("Authentication failed");
        }
        return response.getBody().id();
    }

    @CachePut(value = "boardCache", key = "#result.id")
    public Board create(Board boardIn, String authToken) {
        final String userId = getUserIdFromToken(authToken);
        boardIn.userId(userId);
        return boardRepository.save(new BoardModel(boardIn)).to();
    }

    @CachePut(value = "boardCache", key = "#id")
    public Board update(String id, Board boardIn, String authToken) {
        final String userId = getUserIdFromToken(authToken);
        return boardRepository.findByIdAndUserId(id, userId)
                .map(existingBoardModel -> {
                    existingBoardModel.name(boardIn.name())
                                      .description(boardIn.description());
                    return boardRepository.save(existingBoardModel).to();
                })
                .orElse(null);
    }

    @CacheEvict(value = "boardCache", key = "#id")
    public boolean delete(String id, String authToken) {
        final String userId = getUserIdFromToken(authToken);
        return boardRepository.findByIdAndUserId(id, userId)
                .map(board -> {
                    boardRepository.deleteById(board.id());
                    return true;
                })
                .orElse(false);
    }

    @Cacheable(value = "boardCache", key = "#userId")
    public List<Board> findAll(String authToken) {
        final String userId = getUserIdFromToken(authToken);
        return boardRepository.findAllByUserId(userId)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(BoardModel::to)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "boardCache", key = "#id")
    public Board findById(String id, String authToken) {
        final String userId = getUserIdFromToken(authToken);
        return boardRepository.findByIdAndUserId(id, userId)
                .map(BoardModel::to)
                .orElse(null);
    }

    @Cacheable(value = "boardCache", key = "#name + #sortBy + #userId")
    public List<Board> findByNameContaining(String name, String sortBy, String authToken) {
        final String userId = getUserIdFromToken(authToken);
        return boardRepository.findByNameContainingAndUserId(name, Sort.by(sortBy), userId)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(BoardModel::to)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "boardCache", key = "#userId")
    public List<Board> findOrderByName(String authToken) {
        final String userId = getUserIdFromToken(authToken);
        return boardRepository.findByUserIdOrderByNameDesc(userId)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(BoardModel::to)
                .collect(Collectors.toList());
    }
}
