package com.example.basicpjt.controller;

import com.example.basicpjt.dto.board.BoardDTO;
import com.example.basicpjt.dto.board.BoardListAllDTO;
import com.example.basicpjt.dto.page.PageRequestDTO;
import com.example.basicpjt.dto.page.PageResponseDTO;
import com.example.basicpjt.service.board.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardRestController {

    @Value("${com.example.upload.path}")
    private String uploadPath;

    private final BoardService boardService;

    @GetMapping("/list")
    public PageResponseDTO<BoardListAllDTO> list(PageRequestDTO pageRequestDTO) {
        return boardService.listWithAll(pageRequestDTO);
    }

    @GetMapping("/{bno}")
    public BoardDTO read(@PathVariable Long bno) {
        return boardService.readOne(bno);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Long> registerPost(@Valid @RequestBody BoardDTO boardDTO, BindingResult bindingResult) throws BindException {
        if(bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        Map<String, Long> resultMap = new HashMap<>();

        Long bno = boardService.register(boardDTO);

        resultMap.put("bno", bno);

        return resultMap;
    }

    @PreAuthorize("principal.username == @boardServiceImpl.readOne(#bno).writer")
    @PutMapping(value = "/{bno}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Long> modify(@PathVariable("bno") Long bno, @Valid @RequestBody BoardDTO boardDTO, BindingResult bindingResult) throws BindException {
        boardDTO.setBno(bno);

        if(bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        boardService.modify(boardDTO);

        Map<String, Long> resultMap = new HashMap<>();

        resultMap.put("bno", bno);

        return resultMap;
    }

    @PreAuthorize("principal.username == @boardServiceImpl.readOne(#bno).writer")
    @DeleteMapping("/{bno}")
    public Map<String, Long> remove(@PathVariable("bno") Long bno) {
        List<String> fileNames = boardService.readOne(bno).getFileNames();

        boardService.remove(bno);

        if(fileNames != null && !fileNames.isEmpty()){
            removeFiles(fileNames);
        }

        Map<String, Long> resultMap = new HashMap<>();

        resultMap.put("bno", bno);

        return resultMap;
    }

    public void removeFiles(List<String> files) {
        for(String fileName: files) {
            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

            try {
                String contentType = Files.probeContentType(resource.getFile().toPath());

                resource.getFile().delete();

                if(contentType.startsWith("image")) {
                    File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);

                    thumbnailFile.delete();
                }
            } catch(Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
