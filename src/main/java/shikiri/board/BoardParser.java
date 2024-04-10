package shikiri.board;

public class BoardParser {

    public static Board to(BoardIn in) {
        return Board.builder()
            .name(in.name())
            .description(in.description())
            .userId(in.userId())
            .build();
    }

    public static BoardOut to(Board board) {
        return BoardOut.builder()
            .id(board.id())
            .name(board.name())
            .userId(board.userId())
            .build();
    }
    
}
