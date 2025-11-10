CREATE UNIQUE INDEX uc_orders_user_id ON orders(user_id);
CREATE UNIQUE INDEX uc_orders_status ON orders(status);
CREATE UNIQUE INDEX uc_order_items_order_id ON order_items(order_id);
CREATE UNIQUE INDEX uc_order_items_item_id ON order_items(item_id);