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
	product_status varchar(50) not null check (product_status in ('ACTIVE', 'SOLD', 'REMOVED', 'EXPIRED', 'HELD')),
	auction_end date,
	app_user_id int not null,
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
	expire_date timestamp not null,
	app_user_id int not null,
	product_id int not null,
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
	order_status varchar(50) not null check (order_status in ('PENDING', 'SUCCESS', 'FAILED')),
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
