drop schema if exists grab_it cascade;
create schema grab_it;
set search_path to grab_it;

create table category (
	category_id serial primary key,
	category_name varchar(50) unique not null
);

create table app_user (
	app_user_id serial primary key,
	username varchar(50) unique not null,
	email varchar(254) unique not null,
	password_hash varchar(2048) not null,
	disabled boolean not null default false
);

create table app_role (
	app_role_id serial primary key,
	role_name varchar(50) unique not null
);

create table app_user_role (
	app_user_id int not null,
	app_role_id int not null,
	primary key (app_user_id, app_role_id),
	foreign key (app_user_id) references app_user(app_user_id),
	foreign key (app_role_id) references app_role(app_role_id)
);

create table address (
	address_id serial primary key,
	street varchar(50) not null,
	city varchar(50) not null,
	"state" varchar(50) not null,
	zip_code varchar(50) not null,
	country varchar(50) not null,
	app_user_id int not null,
	foreign key (app_user_id) references app_user(app_user_id) on delete cascade
);

create table product (
	product_id serial primary key,
	posted_at timestamp default current_timestamp,
	sale_type varchar(50) not null check (sale_type in ('BUY_NOW', 'AUCTION')),
	product_name varchar(100) not null,
	description varchar(500) not null,
	price numeric(10,2) not null,
	product_condition varchar(50) not null check (product_condition in ('NEW', 'GOOD', 'EXCELLENT', 'FAIR', 'USED', 'REFURBISHED', 'DAMAGED')),
	quantity int not null,
	category_id int not null,
	product_status varchar(50) not null DEFAULT 'ACTIVE' check (product_status in ('ACTIVE', 'SOLD', 'REMOVED', 'EXPIRED', 'HELD')),
	auction_end timestamp,
	app_user_id int not null,
	winning_bid numeric(10, 2),
	offer_price numeric(10, 2),
	foreign key (app_user_id) references app_user(app_user_id) on delete cascade
);


create table product_category (
	product_category_id serial primary key,
	product_id int not null,
	category_id int not null,
	foreign key (product_id) references product(product_id) on delete cascade,
	foreign key (category_id) references category(category_id) on delete cascade
);

create table bid (
	bid_id serial primary key,
	bid_amount numeric(10,2) not null,
	placed_at timestamp default current_timestamp,
	product_id int not null,
	app_user_id int not null,
	foreign key (product_id) references product(product_id) on delete cascade,
	foreign key (app_user_id) references app_user(app_user_id) on delete cascade
);

create table offer (
	offer_id serial primary key,
	offer_amount numeric(10,2) not null,
	sent_at timestamp default current_timestamp,
	offer_message varchar(200),
	expire_date timestamp default (current_date + interval '2 days'),
	app_user_id int not null,
	product_id int not null,
	offer_status varchar(50) not null DEFAULT 'PENDING' check (offer_status in ('PENDING', 'REJECTED', 'ACCEPTED', 'EXPIRED')),
	foreign key (app_user_id) references app_user(app_user_id) on delete cascade,
	foreign key (product_id) references product(product_id) on delete cascade
);

create table purchase_order (
	order_id serial primary key,
	app_user_id int not null,
	ordered_at timestamp default current_timestamp,
	shipping_address_id int not null,
	billing_address_id int not null,
	total_amount numeric(10,2) not null,
	order_status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (order_status IN ('PENDING', 'SUCCESS', 'FAILED')),
	foreign key (app_user_id) references app_user(app_user_id),
	foreign key (shipping_address_id) references address(address_id),
	foreign key (billing_address_id) references address(address_id)
);

create table payment (
	payment_id serial primary key,
	order_id int not null,
	amount_paid numeric(10,2) not null,
	paid_at timestamp default current_timestamp,
	foreign key (order_id) references purchase_order(order_id) on delete cascade
);

create table shipment (
	shipment_id serial primary key,
	order_id int not null,
	shipment_status varchar(50) not null check (shipment_status in ('PENDING', 'SHIPPED', 'IN_TRANSIT', 'OUT_FOR_DELIVERY', 'DELIVERED')),
	tracking_number varchar(100) not null,
	shipped_at timestamp default current_timestamp,
	delivered_at timestamp,
	foreign key (order_id) references purchase_order(order_id) on delete cascade
);

create table order_product (
	order_product_id serial primary key,
	order_id int not null,
	product_id int not null,
	quantity int not null,
	unit_price numeric(10,2) not null,
	sub_total numeric(10,2) not null,
	foreign key (order_id) references purchase_order(order_id) on delete cascade,
	foreign key (product_id) references product(product_id) on delete cascade
);

create table shopping_cart (
	shopping_cart_id serial primary key,
	product_id int not null,
	quantity int not null,
	app_user_id int not null,
	unique (product_id, app_user_id),
	foreign key (product_id) references product(product_id) on delete cascade,
	foreign key (app_user_id) references app_user(app_user_id) on delete cascade
);

create table watchlist (
	watch_id serial primary key,
	product_id int not null,
	app_user_id int not null,
	unique (product_id, app_user_id),
	foreign key (product_id) references product(product_id) on delete cascade,
	foreign key (app_user_id) references app_user(app_user_id) on delete cascade
);

create table review (
	review_id serial primary key,
	rating int not null,
	review_text varchar(1000) not null,
	posted_by_id int not null,
	seller_id int not null,
	product_id int not null,
	created_at timestamp default current_timestamp,
	foreign key (posted_by_id) references app_user(app_user_id) on delete cascade,
	foreign key (seller_id) references app_user(app_user_id) on delete cascade,
	foreign key (product_id) references product(product_id) on delete cascade
);

create table coupon (
	coupon_id serial primary key,
	coupon_code varchar(50) not null,
	discount int not null,
	discount_type varchar(50) not null check (discount_type in ('FIXED', 'PERCENTAGE')),
	expire_date timestamp not null,
	is_active boolean not null
);

create table image (
	image_id serial primary key,
	image_url varchar(100) not null,
	product_id int not null,
	foreign key (product_id) references product(product_id) on delete cascade
);


INSERT INTO category (category_name) VALUES
	('Electronics'),
	('Books'),
	('Clothing');

	INSERT INTO app_user (username, email, password_hash) VALUES
	('alice', 'alice@example.com', '$2y$10$5XmabI6UghCVaIDJrvgHxeWe.vhe6Htd.QANZJ4RIkPzPHtpirP0y'),
	('bob', 'bob@example.com', '$2y$10$5XmabI6UghCVaIDJrvgHxeWe.vhe6Htd.QANZJ4RIkPzPHtpirP0y'),
	('dono2223', 'dono2223@gmail.com', '$2y$10$5XmabI6UghCVaIDJrvgHxeWe.vhe6Htd.QANZJ4RIkPzPHtpirP0y'),
	('kevin123', 'kevin123@gmail.com', '$2y$10$5XmabI6UghCVaIDJrvgHxeWe.vhe6Htd.QANZJ4RIkPzPHtpirP0y');

	INSERT INTO app_role (role_name) VALUES
	('USER'),
	('SELLER'),
	('ADMIN');

	INSERT INTO app_user_role (app_user_id, app_role_id) VALUES
	(1, 1),
	(2, 2),
	(2, 1),
	(3, 3),
	(4, 1),
	(4, 2);

	INSERT INTO address (street, city, "state", zip_code, country, app_user_id) VALUES
	('123 Main St', 'Springfield', 'IL', '62704', 'USA', 1),
	('456 Main St', 'Springfield', 'IL', '62704', 'USA', 4),
	('456 Elm St', 'Greenville', 'TX', '75401', 'USA', 2);


	INSERT INTO product (sale_type, product_name, description, price, product_condition, quantity, category_id, product_status, auction_end, winning_bid, offer_price, app_user_id) VALUES
	('BUY_NOW', 'Laptop', 'Powerful gaming laptop', 1200.00, 'EXCELLENT', 1, 1, 'ACTIVE', NULL, NULL, NULL, 2),
	('BUY_NOW', 'PC', 'Powerful gaming PC', 1500.00, 'EXCELLENT', 1, 1, 'SOLD', NULL, NULL, NULL, 2),
	('AUCTION', 'Book Set', 'Complete fantasy trilogy', 30.00, 'GOOD', 1, 2, 'ACTIVE', current_date + interval '2 day', NULL, NULL, 2);

	INSERT INTO product_category (product_id, category_id) VALUES
	(1, 1),
	(2, 1),
	(3, 2);

	INSERT INTO offer (offer_amount, offer_message, expire_date, app_user_id, product_id, offer_status) VALUES
	(900.00, 'Would you take less please im poor?', current_timestamp + interval '2 day', 4, 1, 'PENDING');

	INSERT INTO purchase_order (app_user_id, shipping_address_id, billing_address_id, total_amount, order_status) VALUES
	(1, 1, 1, 1500.00, 'PENDING');

	INSERT INTO payment (order_id, amount_paid) VALUES
	(1, 1500.00);

	INSERT INTO shipment (order_id, shipment_status, tracking_number) VALUES
	(1, 'PENDING', 'TRACK1234511111111');

	INSERT INTO order_product (order_id, product_id, quantity, unit_price, sub_total) VALUES
	(1, 2, 1, 1500.00, 1500.00);

	INSERT INTO shopping_cart (product_id, quantity, app_user_id) VALUES
	(1, 1, 1),
	(3, 3, 2),
	(1, 3, 2);

	INSERT INTO watchlist (product_id, app_user_id) VALUES
	(3, 2);

	INSERT INTO review (rating, review_text, posted_by_id, seller_id, product_id) VALUES
	(5, 'Great seller, cant wait!', 1, 2, 2),
	(1, 'Terrible FUCING WOKEKEKKE', 1, 2, 3);

	INSERT INTO coupon (coupon_code, discount, discount_type, expire_date, is_active) VALUES
	('SAVE10', 10, 'PERCENTAGE', current_timestamp + interval '7 day', true);

	INSERT INTO image (image_url, product_id) VALUES
	('http://example.com/laptop.jpg', 2),
	('http://example.com/pc.jpg', 2),
	('https://res.cloudinary.com/dhucaqc0o/image/upload/v1752634858/debwthjfruov0qbju9fn.png', 3);



select * from purchase_order;

select * from shipment;

select * from order_product;

select * from payment;

select * from app_user;

select * from product;

select * from offer;

select * from review;

select * from image;

select * from bid;

select * from app_user;

select * from app_user_role;

select * from product_category;

select * from shopping_cart;
