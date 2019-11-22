create table VehicleType (
	VehicleType_vtname varchar(20) not null PRIMARY KEY check (VehicleType_vtname in ('Economy', 'Compact', 'Mid-size', 'Standard', 'Fullsize', 'SUV', 'Truck')),
	VehicleType_features varchar(50),
	VehicleType_wrate integer not null,
	VehicleType_drate integer not null,
	VehicleType_hrate integer not null,
	VehicleType_wirate integer not null,
	VehicleType_dirate integer not null,
	VehicleType_hirate integer not null,
	VehicleType_krate decimal not null
);

create table Vehicle ( 
	Vehicle_vlicense varchar(10) not null PRIMARY KEY,
	Vehicle_make varchar(30),
	Vehicle_model varchar(20),
	Vehicle_year integer,
	Vehicle_color varchar(10),
	Vehicle_odometer integer,
	Vehicle_status varchar(20) not null check (Vehicle_status in ('rented', 'available', 'maintenance')), 
	VehicleType_vtname varchar(20) not null,
	Vehicle_location varchar(50) not null,
	Vehicle_city varchar(20) not null,
	foreign key (VehicleType_vtname) references VehicleType
);

create table Customer ( 
	Customer_dlicense integer not null PRIMARY KEY,
    Customer_cellphone varchar(20),
	Customer_name varchar(20) not null,
	Customer_address varchar(50)
);


create table Reservation ( 
	Reservation_confNo integer not null PRIMARY KEY,
	VehicleType_vtname varchar(20) not null check (VehicleType_vtname in ('Economy', 'Compact', 'Mid-size', 'Standard', 'Fullsize', 'SUV', 'Truck')),
	Customer_dlicense integer not null,
	Reservation_fromDate date not null,
	Reservation_toDate date not null,
	foreign key (VehicleType_vtname) references VehicleType,
	foreign key (Customer_dlicense) references Customer
);

create table Rental (
	Rental_rid integer not null PRIMARY KEY,
	Vehicle_vlicense varchar(10) not null,
	Customer_dlicense integer not null,
	Rental_odometer integer not null,
	Rental_cardName varchar(10) not null check (Rental_cardName in ('MasterCard', 'Visa')),
	Rental_cardNo integer not null,
	Rental_ExpDate date not null,
	Rental_fromDate date not null,
	Rental_toDate date not null,
	Reservation_confNo integer not null,
	foreign key (Vehicle_vlicense) references Vehicle,
	foreign key (Customer_dlicense) references Customer,
	foreign key (Reservation_confNo) references Reservation
);

create table Return (
	Rental_rid integer not null,
	Return_date date,
	Return_odometer integer not null,
	Return_fulltank varchar(10) not null check (Return_fulltank in ('True', 'False')),
	Return_value Number not null,
	foreign key (Rental_rid) references Rental
);

commit;

