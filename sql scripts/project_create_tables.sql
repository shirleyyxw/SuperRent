create table VehicleType (
vtname varchar(20) not null PRIMARY KEY check (vtname in ('Economy', 'Compact', 'Mid-size', 'Standard', 'Fullsize', 'SUV', 'Truck')),
features varchar(50),
wrate integer not null,
drate integer not null,
hrate integer not null,
wirate integer not null,
dirate integer not null,
hirate integer not null,
krate decimal not null
);

create table Vehicle (
vlicense varchar(10) not null PRIMARY KEY,
make varchar(30),
model varchar(20),
productionYear integer,
color varchar(10),
odometer integer,
status varchar(20) not null check (status in ('rented', 'available', 'maintenance')),
vtname varchar(20) not null,
location varchar(50) not null,
city varchar(20) not null,
foreign key (vtname) references VehicleType
);

create table Customer (
dlicense integer not null PRIMARY KEY,
cellphone varchar(20),
name varchar(20) not null,
address varchar(50)
);


create table Reservation (
confNo integer not null PRIMARY KEY,
vtname varchar(20) not null check (vtname in ('Economy', 'Compact', 'Mid-size', 'Standard', 'Fullsize', 'SUV', 'Truck')),
dlicense integer not null,
fromDate date not null,
toDate date not null,
foreign key (vtname) references VehicleType,
foreign key (dlicense) references Customer
);

create table Rental (
rid integer not null PRIMARY KEY,
vlicense varchar(10) not null,
dlicense integer not null,
odometer integer not null,
cardName varchar(10) not null check (cardName in ('MasterCard', 'Visa')),
cardNo integer not null,
ExpDate date not null,
fromDate date not null,
toDate date not null,
confNo integer not null,
foreign key (vlicense) references Vehicle,
foreign key (dlicense) references Customer,
foreign key (confNo) references Reservation
);

create table Return (
rid integer not null,
returnTime date not null,
odometer integer not null,
fulltank varchar(10) not null check (fulltank in ('True', 'False')),
payment Number not null,
foreign key (rid) references Rental
);

commit;
