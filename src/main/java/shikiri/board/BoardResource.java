package shikiri.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.jsonwebtoken.security.Keys;
import shikiri.board.exceptions.InvalidTokenException;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

@RestController
@RequestMapping("/boards")
public class BoardResource {

    private final SecretKey secretKey;
    private final BoardService boardService;

    @Autowired
    public BoardResource(@Value("${shikiri.jwt.secret}") String secret, BoardService boardService) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.boardService = boardService;
    }

    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody Board boardIn, @RequestHeader HttpHeaders headers) {
        try {
            String userId = BoardUtility.getUserBySecretKey(headers, secretKey);
            if (userId != null) {
                BoardModel savedBoardModel = boardService.saveBoard(new BoardModel(boardIn));
                URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(savedBoardModel.id()).toUri();
                return ResponseEntity.created(location).body(savedBoardModel.toDTO());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Board>> getAllBoards(@RequestHeader HttpHeaders headers) {
        try {
            String userId = BoardUtility.getUserBySecretKey(headers, secretKey);
            if (userId != null) {
                List<Board> boards = boardService.findAllBoards().stream()
                                            .map(BoardModel::toDTO)
                                            .collect(Collectors.toList());
                return ResponseEntity.ok().body(boards);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardById(@PathVariable String id, @RequestHeader HttpHeaders headers) {
        try {
            String userId = BoardUtility.getUserBySecretKey(headers, secretKey);
            if (userId != null) {
                return boardService.findBoardById(id)
                                .map(board -> ResponseEntity.ok().body(board.toDTO()))
                                .orElseGet(() -> ResponseEntity.notFound().build());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @GetMapping("/search/by-name")
    public ResponseEntity<List<Board>> findBoardsByNameContaining(@RequestParam String name,
                                                                @RequestParam(defaultValue = "name") String sortBy,
                                                                @RequestHeader HttpHeaders headers) {
        try {
            String userId = BoardUtility.getUserBySecretKey(headers, secretKey);
            if (userId != null) {
                List<Board> boards = boardService.findBoardsByNameContaining(name, Sort.by(sortBy)).stream()
                                            .map(BoardModel::toDTO)
                                            .collect(Collectors.toList());
                return ResponseEntity.ok().body(boards);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @GetMapping("/all/sorted-by-date")
    public ResponseEntity<List<Board>> findAllBoardsOrderedByCreationDateDesc(@RequestHeader HttpHeaders headers) {
        try {
            String userId = BoardUtility.getUserBySecretKey(headers, secretKey);
            if (userId != null) {
                List<Board> boards = boardService.findAllBoardsOrderedByCreationDateDesc().stream()
                                            .map(BoardModel::toDTO)
                                            .collect(Collectors.toList());
                return ResponseEntity.ok().body(boards);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable String id, @RequestBody Board boardIn, @RequestHeader HttpHeaders headers) {
        try {
            String userId = BoardUtility.getUserBySecretKey(headers, secretKey);
            if (userId != null) {
                Optional<BoardModel> existingBoardModelOptional = boardService.findBoardById(id);
                if (!existingBoardModelOptional.isPresent()) {
                    return ResponseEntity.notFound().build();
                }
                BoardModel existingBoardModel = existingBoardModelOptional.get();
                existingBoardModel.name(boardIn.name());
                existingBoardModel.description(boardIn.description());
                BoardModel updatedBoardModel = boardService.updateBoard(existingBoardModel);
                return ResponseEntity.ok().body(updatedBoardModel.toDTO());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable String id, @RequestHeader HttpHeaders headers) {
        try {
            String userId = BoardUtility.getUserBySecretKey(headers, secretKey);
            if (userId != null) {
                boolean deleted = boardService.deleteBoard(id);
                if (deleted) {
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
