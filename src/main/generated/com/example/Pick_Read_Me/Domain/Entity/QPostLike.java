package com.example.Pick_Read_Me.Domain.Entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPostLike is a Querydsl query type for PostLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostLike extends EntityPathBase<PostLike> {

    private static final long serialVersionUID = 1649232114L;

    public static final QPostLike postLike = new QPostLike("postLike");

    public final NumberPath<Long> githubId = createNumber("githubId", Long.class);

    public final NumberPath<Long> post_id = createNumber("post_id", Long.class);

    public QPostLike(String variable) {
        super(PostLike.class, forVariable(variable));
    }

    public QPostLike(Path<? extends PostLike> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPostLike(PathMetadata metadata) {
        super(PostLike.class, metadata);
    }

}

