package org.zerock.board.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.board.dto.PageRequestDTO;

import java.util.List;

public interface SearchBoardRepository {

    Page<Object[]> searchList(PageRequestDTO pageRequestDTO);
}
