package shikiri.board;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import shikiri.board.exceptions.CustomDataAccessException;
import shikiri.board.exceptions.CustomDataIntegrityViolationException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    // Constructor-based injection is recommended for better testability
    @Autowired
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    // Method to create (or save) a board
    @Transactional
    public BoardModel saveBoard(BoardModel board) {
        try {
            return boardRepository.save(board);
        } catch (DataIntegrityViolationException e) {
            throw new CustomDataIntegrityViolationException("Failed to save board due to data integrity violation.");
        } catch (DataAccessException e) {
            throw new CustomDataAccessException("Failed to access data.");
        }
    }

    // Method to retrieve all boards
    public List<BoardModel> findAllBoards() {
        return boardRepository.findAll();
    }

    // Method to find a board by its ID
    public Optional<BoardModel> findBoardById(String id) {
        return boardRepository.findById(id);
    }
    
    // Method to find boards by name containing a specific string, sorted by a criteria
    public List<BoardModel> findBoardsByNameContaining(String name, Sort sort) {
        return boardRepository.findByNameContaining(name, sort);
    }

    // Method to retrieve all boards ordered by creation date in descending order
    public List<BoardModel> findAllBoardsOrderedByCreationDateDesc() {
        return boardRepository.findAllByOrderByCreatedDateDesc();
    }

    // Method to update a board
    @Transactional
    public BoardModel updateBoard(BoardModel board) {
        return boardRepository.save(board);
    }

    // Method to delete a board by its ID
    @Transactional
    public Boolean deleteBoard(String id) {
        try {
            boardRepository.deleteById(id);
            return true;
        } catch (DataAccessException e) {
            throw new CustomDataAccessException("Failed to access data.");
        }
    }
}
