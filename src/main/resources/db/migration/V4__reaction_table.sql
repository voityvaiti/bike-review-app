CREATE TABLE IF NOT EXISTS reaction
(
    id        BIGSERIAL PRIMARY KEY,
    review_id BIGINT  NOT NULL,
    user_id   INT     NOT NULL,
    is_like   BOOLEAN NOT NULL,
    FOREIGN KEY (review_id) REFERENCES review (id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES usr (id) ON UPDATE CASCADE ON DELETE CASCADE,
    UNIQUE (review_id, user_id)
);