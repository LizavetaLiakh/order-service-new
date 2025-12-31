--liquibase formatted sql

--changeset LizavetaLiakh:ord1_tables
CREATE TABLE orders(
    id BIGINT PRIMARY KEY NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(10) NOT NULL,
    creation_date DATE NOT NULL
);

CREATE TABLE items(
    id BIGINT PRIMARY KEY NOT NULL,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL
);

CREATE TABLE order_items(
    id BIGINT PRIMARY KEY NOT NULL,
    order_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT fk_order_items_order_id FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_item_id FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
);