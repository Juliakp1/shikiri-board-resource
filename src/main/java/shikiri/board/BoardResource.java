package shikiri.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import shikiri.board.exceptions.InvalidTokenException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class BoardResource implements BoardController {

    @Autowired
    private BoardService boardService;

    @Override
    public ResponseEntity<BoardOut> create(String userId, BoardIn boardIn) {
        try {
            Board board = BoardParser.to(boardIn);
            board = boardService.create(board, userId);
            URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(board.id())
                .toUri();
            return ResponseEntity.created(location).body(BoardParser.to(board));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<BoardOut> update(String userId, BoardIn boardIn) {
        try {
            Board board = BoardParser.to(boardIn);
            board = boardService.update(board.id(), board, userId);
            if (board != null) {
                return ResponseEntity.ok(BoardParser.to(board));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<BoardOut> delete (String userId, String id) {
        try {
            boolean deleted = boardService.delete(id, userId);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<BoardOut> getBoardById(String userId, String id) {
        try {
            Board board = boardService.findById(id, userId);
            if (board != null) {
                return ResponseEntity.ok(BoardParser.to(board));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<List<BoardOut>> findBoardsByNameContaining(String userId, String name, String sortBy) {
        try {
            List<Board> boards = boardService.findByNameContaining(name, sortBy, userId);
            List<BoardOut> boardsOut = boards.stream().map(BoardParser::to).collect(Collectors.toList());
            return ResponseEntity.ok(boardsOut);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<List<BoardOut>> findOrderByName(String userId) {
        try {
            List<Board> boards = boardService.findOrderByName(userId);
            List<BoardOut> boardsOut = boards.stream().map(BoardParser::to).collect(Collectors.toList());
            return ResponseEntity.ok(boardsOut);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
