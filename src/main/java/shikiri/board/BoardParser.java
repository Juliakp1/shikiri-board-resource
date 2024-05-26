package shikiri.board;

public class BoardParser {

    public static Board to(BoardIn in) {
        return Board.builder()
            .name(in.name())
            .description(in.description())
            .build();
    }

    public static BoardOut to(Board board) {
        return BoardOut.builder()
            .id(board.id())
            .name(board.name())
            .description(board.description())
            .userId(board.userId())
            .build();
    }
    
}
