package org.zerock.board.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Table(name = "tbl_member")
public class Member extends BaseEntity {

    @Id
    private String email;

    private String password;

    private String name;

}
