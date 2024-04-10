package shikiri.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "board")
@EqualsAndHashCode(of = "id")
@Builder @Getter @Setter @Accessors(chain = true, fluent = true)
@NoArgsConstructor @AllArgsConstructor
public class BoardModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_board")
    private String id;

    @Column(name = "bd_name")
    private String name;

    @Column(name = "bd_description")
    private String description;

    @Column(name = "bd_userId")
    private String userId;

    public BoardModel(Board o) {
        this.id = o.id();
        this.name = o.name();
        this.description = o.description();
        this.userId = o.userId();
    }
    
    public Board toDTO() {
        return Board.builder()
            .id(id)
            .name(name)
            .description(description)
            .userId(userId)
            .build();
    }
}
