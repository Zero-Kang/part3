package org.zerock.board.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.board.dto.PageRequestDTO;
import org.zerock.board.entity.Board;
import org.zerock.board.entity.QBoard;
import org.zerock.board.entity.QMember;
import org.zerock.board.entity.QReply;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Log4j2
public class SearchBoardRepositoryImpl extends QuerydslRepositorySupport implements SearchBoardRepository{

    public SearchBoardRepositoryImpl() {
        super(Board.class);
    }

    @Override
    public Page<Object[]> searchList(PageRequestDTO pageRequestDTO) {
        log.info("log4j2...............");

        QBoard board = QBoard.board;
        QMember member = QMember.member;
        QReply reply = QReply.reply;

        JPQLQuery<Board> jpqlQuery = from(board);
        jpqlQuery.join(member).on(board.writer.eq(member));
        jpqlQuery.leftJoin(reply).on(reply.board.eq(board));

        String type = pageRequestDTO.getType();
        String keyword = pageRequestDTO.getKeyword();

        JPQLQuery<Tuple> tuple = jpqlQuery.select(board ,  reply.count());
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        BooleanExpression expression = board.bno.gt(0L);

        booleanBuilder.and(expression);

        if(type != null){
            String[] typeArr = type.split("");
            //검색 조건을 작성하기
            BooleanBuilder conditionBuilder = new BooleanBuilder();

            for (String t:typeArr) {
                switch (t){
                    case "t":
                        conditionBuilder.or(board.title.contains(keyword));
                        break;
                    case "w":
                        conditionBuilder.or(member.email.contains(keyword));
                        break;
                    case "c":
                        conditionBuilder.or(board.content.contains(keyword));
                        break;
                }
            }
            booleanBuilder.and(conditionBuilder);
        }

        tuple.where(booleanBuilder);
        tuple.groupBy(board);

        Pageable pageable = pageRequestDTO.getPageable(Sort.by("bno").descending());

        tuple.offset(pageable.getOffset());
        tuple.limit(pageable.getPageSize());
        jpqlQuery.orderBy(board.bno.desc());

        log.info("\n" + tuple);

        List<Tuple> list = tuple.fetch();

        long total = tuple.fetchCount();

        return new PageImpl<Object[]>(list.stream().map(t -> t.toArray()).collect(Collectors.toList()), pageable, total);

    }
}
