package shikiri.board;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;

@Service
public class BoardService {

    private BoardRepository boardRepository;
    private SecretKey secretKey;

    @Autowired
    public BoardService(BoardRepository boardRepository, @Value("${shikiri.jwt.secretKey}") String secret) {
        this.boardRepository = boardRepository;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Board create(Board boardIn, String authToken) {
        boardIn.userId(BoardUtility.getUserIdFromToken(authToken, secretKey));
        return boardRepository.save(new BoardModel(boardIn)).to();
    }

    public Board update(String id, Board boardIn, String authToken) {
        String userId = BoardUtility.getUserIdFromToken(authToken, secretKey);
        return boardRepository.findByIdAndUserId(id, userId)
                .map(existingBoardModel -> {
                    existingBoardModel.name(boardIn.name())
                                      .description(boardIn.description());
                    return boardRepository.save(existingBoardModel).to();
                })
                .orElse(null);
    }

    public boolean delete(String id, String authToken) {
        String userId = BoardUtility.getUserIdFromToken(authToken, secretKey);
        return boardRepository.findByIdAndUserId(id, userId)
                .map(board -> {
                    boardRepository.deleteById(board.id());
                    return true;
                })
                .orElse(false);
    }

    public List<Board> findAll(String authToken) {
        String userId = BoardUtility.getUserIdFromToken(authToken, secretKey);
        return boardRepository.findAllByUserId(userId)
                .orElseGet(Collections::emptyList) // Return an empty list if Optional is empty
                .stream()
                .map(BoardModel::to)
                .collect(Collectors.toList());
    }

    public Board findById(String id, String authToken) {
        String userId = BoardUtility.getUserIdFromToken(authToken, secretKey);
        return boardRepository.findByIdAndUserId(id, userId)
                .map(BoardModel::to)
                .orElse(null); // Return null if Optional is empty
    }

    public List<Board> findByNameContaining(String name, String sortBy, String authToken) {
        String userId = BoardUtility.getUserIdFromToken(authToken, secretKey);
        return boardRepository.findByNameContainingAndUserId(name, Sort.by(sortBy), userId)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(BoardModel::to)
                .collect(Collectors.toList());
    }

    public List<Board> findOrderByName(String authToken) {
        String userId = BoardUtility.getUserIdFromToken(authToken, secretKey);
        return boardRepository.findByUserIdOrderByNameDesc(userId)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(BoardModel::to)
                .collect(Collectors.toList());
    }
}
