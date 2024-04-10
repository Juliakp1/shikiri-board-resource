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
    public ResponseEntity<BoardOut> create(String authToken, BoardIn boardIn) {
        try {
            Board board = BoardParser.to(boardIn);
            board = boardService.create(board, authToken);
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
    public ResponseEntity<BoardOut> update(String authToken, BoardIn boardIn) {
        try {
            Board board = BoardParser.to(boardIn);
            board = boardService.update(board.id(), board, authToken);
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
    public ResponseEntity<BoardOut> delete (String authToken, String id) {
        try {
            boolean deleted = boardService.delete(id, authToken);
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
    public ResponseEntity<BoardOut> getBoardById(String authToken, String id) {
        try {
            Board board = boardService.findById(id, authToken);
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
    public ResponseEntity<List<BoardOut>> findBoardsByNameContaining(String authToken, String name, String sortBy) {
        try {
            List<Board> boards = boardService.findByNameContaining(name, sortBy, authToken);
            List<BoardOut> boardsOut = boards.stream().map(BoardParser::to).collect(Collectors.toList());
            return ResponseEntity.ok(boardsOut);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<List<BoardOut>> findAllBoardsOrderedByCreationDateDesc(String authToken) {
        try {
            List<Board> boards = boardService.findOrderByName(authToken);
            List<BoardOut> boardsOut = boards.stream().map(BoardParser::to).collect(Collectors.toList());
            return ResponseEntity.ok(boardsOut);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
