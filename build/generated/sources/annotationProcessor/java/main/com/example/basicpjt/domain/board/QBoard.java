package com.example.basicpjt.domain.board;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoard is a Querydsl query type for Board
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoard extends EntityPathBase<Board> {

    private static final long serialVersionUID = -636046971L;

    public static final QBoard board = new QBoard("board");

    public final com.example.basicpjt.domain.QBaseEntity _super = new com.example.basicpjt.domain.QBaseEntity(this);

    public final NumberPath<Long> bno = createNumber("bno", Long.class);

    public final StringPath content = createString("content");

    public final SetPath<BoardImage, QBoardImage> imageSet = this.<BoardImage, QBoardImage>createSet("imageSet", BoardImage.class, QBoardImage.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final SetPath<com.example.basicpjt.domain.reply.Reply, com.example.basicpjt.domain.reply.QReply> replySet = this.<com.example.basicpjt.domain.reply.Reply, com.example.basicpjt.domain.reply.QReply>createSet("replySet", com.example.basicpjt.domain.reply.Reply.class, com.example.basicpjt.domain.reply.QReply.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    public final StringPath writer = createString("writer");

    public QBoard(String variable) {
        super(Board.class, forVariable(variable));
    }

    public QBoard(Path<? extends Board> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBoard(PathMetadata metadata) {
        super(Board.class, metadata);
    }

}

