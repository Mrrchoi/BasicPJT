package com.example.basicpjt.service.reply;

import com.example.basicpjt.dto.page.PageRequestDTO;
import com.example.basicpjt.dto.page.PageResponseDTO;
import com.example.basicpjt.dto.reply.ReplyDTO;

public interface ReplyService {

    Long register(ReplyDTO replyDTO);

    ReplyDTO read(Long rno);

    void modify(ReplyDTO replyDTO);

    void remove(Long rno);

    PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO);
}