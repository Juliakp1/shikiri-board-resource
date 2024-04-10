CREATE TABLE board
(
    id_tool character varying(36) NOT NULL,
    bd_name character varying(256) NOT NULL,
    bd_description character varying(512) NOT NULL,
    bd_userId character varying(36) NOT NULL,
    CONSTRAINT board_pkey PRIMARY KEY (id_board)
);
