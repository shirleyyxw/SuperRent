
Insert into VehicleType values('Economy', 'heated seat', 250, 50, 10, 80, 20, 5, 0.5);
Insert into VehicleType values('Compact', 'heated seat', 300, 60, 15, 90, 25, 8, 1);
Insert into VehicleType values('Mid-size', 'dual CD player', 350, 70, 20, 100, 30, 10, 1.5);
Insert into VehicleType values('Standard', 'dual CD player', 400, 80, 25, 110, 35, 15, 2);
Insert into VehicleType values('Fullsize', 'navigation system', 450, 90, 30, 120, 40, 20, 2.5);
Insert into VehicleType values('SUV', 'navigation system', 500, 100, 35, 130, 45, 25, 3);
Insert into VehicleType values('Truck', 'backup camera', 550, 110, 40, 140, 50, 30, 3.5);

Insert into Vehicle values('ABC-101', 'Toyota', 'Prius', 2015, 'grey', 20000, 'available', 'Economy', '2525 WestMall in front of starbucks', 'Vancouver');
Insert into Vehicle values('ABC-102', 'Toyota', 'Prius', 2015, 'grey', 20000, 'available', 'Economy', '2525 WestMall in front of starbucks', 'Vancouver');
Insert into Vehicle values('ABC-103', 'Toyota', 'Prius', 2015, 'grey', 20000, 'available', 'Economy', '2525 WestMall in front of starbucks', 'Vancouver');
Insert into Vehicle values('ABC-104', 'Toyota', 'Prius', 2015, 'grey', 20000, 'available', 'Mid-size', '2525 WestMall in front of starbucks', 'Vancouver');
Insert into Vehicle values('ABC-105', 'Toyota', 'Prius', 2015, 'grey', 20000, 'available', 'Fullsize', '2525 WestMall in front of starbucks', 'Vancouver');
Insert into Vehicle values('ABC-106', 'Toyota', 'Prius', 2015, 'grey', 20040, 'available', 'Compact', '2525 WestMall in front of starbucks', 'Vancouver');
Insert into Vehicle values('ABC-107', 'Toyota', 'Camry', 2017, 'white', 1000, 'available', 'SUV', '2525 WestMall in front of starbucks', 'Vancouver');
Insert into Vehicle values('ABC-108', 'Toyota', 'Camry', 2017, 'grey', 5000, 'available', 'Truck', '2525 WestMall in front of starbucks', 'Vancouver');
Insert into Vehicle values('ABC-109', 'Toyota', 'Prius', 2015, 'black', 234521, 'available', 'Standard', '1680 EastMall Parking', 'Vancouver');
Insert into Vehicle values('ABC-110', 'Toyota', 'Prius Plus', 2016, 'black', 0, 'maintenance', 'SUV', '1680 EastMall Parking', 'Vancouver');
Insert into Vehicle values('ABC-111', 'Toyota', 'Prius', 2015, 'black', 234521, 'available', 'SUV', '1680 EastMall Parking', 'Vancouver');
Insert into Vehicle values('ABC-112', 'Toyota', 'Avalon', 2013, 'red', 30000, 'available', 'Mid-size', '1680 EastMall Parking', 'Vancouver');
Insert into Vehicle values('ABC-113', 'Toyota', 'Prius', 2014, 'blue', 5600, 'available', 'Fullsize', '1680 EastMall Parking', 'Vancouver');
Insert into Vehicle values('ABC-114', 'Toyota', 'Prius c', 2013, 'black', 4300, 'available', 'Fullsize', '1680 EastMall Parking', 'Vancouver');
Insert into Vehicle values('ABC-115', 'Toyota', 'HighLander', 2015, 'grey', 34000, 'available', 'Compact', '1680 EastMall Parking', 'Vancouver');
Insert into Vehicle values('ABC-116', 'Toyota', 'Prius', 2015, 'black', 234521, 'available', 'Compact', '1680 EastMall Parking', 'Vancouver');
Insert into Vehicle values('ABC-117', 'Toyota', 'Prius', 2015, 'black', 234521, 'available', 'Truck', '1680 EastMall Parking', 'Vancouver');


Insert into Customer values(1000001, '(604)-000-000', 'Bob', '1234 Wesbrook Mall');
Insert into Customer values(1000002, '(604)-000-001', 'Sally', '2345 Wesbrook Mall');
Insert into Customer values(1000003, '(604)-000-002', 'Tony', '3456 Wesbrook Mall');
Insert into Customer values(1000004, '(778)-000-003', 'David', '4567 Wesbrook Mall');
Insert into Customer values(1000005, '(604)-000-004', 'Asher', '5678 Wesbrook Mall');
Insert into Customer values(1000006, '(778)-000-005', 'Linda', '6789 Wesbrook Mall');
Insert into Customer values(1000007, '(604)-000-006', 'Edward', '7890 Wesbrook Mall');
Insert into Customer values(1000008, '(604)-000-007', 'Emma', '8910 Wesbrook Mall');
Insert into Customer values(1000009, '(604)-000-008', 'Sara', '8911 Wesbrook Mall');
Insert into Customer values(1000010, '(604)-000-009', 'Rex', '8912 Wesbrook Mall');

Insert into Reservation values (11111111, 'Economy', 1000001,TO_DATE('01-NOV-2019 13:35:00', 'DD-MON-YYYY HH24:MI:SS'), TO_DATE('01-DEC-2019 13:35:00', 'DD-MON-YYYY HH24:MI:SS'));
Insert into Reservation values (22222222, 'Compact', 1000002, TO_DATE('01-NOV-2019 08:31:00', 'DD-MON-YYYY HH24:MI:SS'), TO_DATE('01-DEC-2019 09:12:00', 'DD-MON-YYYY HH24:MI:SS'));
Insert into Reservation values (33333333, 'Mid-size', 1000003, TO_DATE('24-DEC-2019 07:05:00', 'DD-MON-YYYY HH24:MI:SS'), TO_DATE('15-JAN-2020 19:38:00', 'DD-MON-YYYY HH24:MI:SS'));
Insert into Reservation values (44444444, 'Standard', 1000004, TO_DATE('01-OCT-2019 20:00:00', 'DD-MON-YYYY HH24:MI:SS'), TO_DATE('10-OCT-2019 20:00:00', 'DD-MON-YYYY HH24:MI:SS'));
Insert into Reservation values (55555555, 'Fullsize', 1000005, TO_DATE('01-OCT-2019 14:00:00', 'DD-MON-YYYY HH24:MI:SS'), TO_DATE('10-OCT-2019 14:00:00', 'DD-MON-YYYY HH24:MI:SS'));
Insert into Reservation values (66666666, 'SUV', 1000006, TO_DATE('01-NOV-2019 08:00:00', 'DD-MON-YYYY HH24:MI:SS'), TO_DATE('01-DEC-2019 15:00:00', 'DD-MON-YYYY HH24:MI:SS'));
Insert into Reservation values (77777777, 'Truck', 1000007, TO_DATE('03-JAN-2020 13:00:00', 'DD-MON-YYYY HH24:MI:SS'), TO_DATE('31-JAN-2020 12:15:00', 'DD-MON-YYYY HH24:MI:SS'));
Insert into Reservation values (88888888, 'Economy', 1000008, TO_DATE('01-DEC-2019 13:35:00', 'DD-MON-YYYY HH24:MI:SS'), TO_DATE('5-DEC-2019 13:35:00', 'DD-MON-YYYY HH24:MI:SS'));
Insert into Reservation values (99999999, 'Compact', 1000009, TO_DATE('18-DEC-2019 13:35:00', 'DD-MON-YYYY HH24:MI:SS'), TO_DATE('20-DEC-2019 13:35:00', 'DD-MON-YYYY HH24:MI:SS'));
Insert into Reservation values (12121212, 'Mid-size', 1000010, TO_DATE('01-JAN-2020 13:35:00', 'DD-MON-YYYY HH24:MI:SS'), TO_DATE('15-JAN-2019 13:35:00', 'DD-MON-YYYY HH24:MI:SS'));
Insert into Reservation values (13131313, 'Standard', 1000010, TO_DATE('02-DEC-2019 13:35:00', 'DD-MON-YYYY HH24:MI:SS'), TO_DATE('05-DEC-2019 13:35:00', 'DD-MON-YYYY HH24:MI:SS'));

Insert into Rental values(10001, 'ABC-101', 1000001, 20000, 'MasterCard', 123456, TO_DATE('01-NOV-2022', 'DD-MON-YYYY'), TO_DATE('01-NOV-2019 13:35:00', 'DD-MON-YYYY HH24:MI:SS'), TO_DATE('01-DEC-2019 13:35:00', 'DD-MON-YYYY HH24:MI:SS'), 11111111);
Insert into Rental values(10002, 'ABC-115', 1000002, 34000, 'Visa', 234567, TO_DATE('01-NOV-2023', 'DD-MON-YYYY'), TO_DATE('01-NOV-2019 08:31:00', 'DD-MON-YYYY HH24:MI:SS'), TO_DATE('01-DEC-2019 09:12:00', 'DD-MON-YYYY HH24:MI:SS'), 22222222);
Insert into Rental values(10003, 'ABC-107', 1000006, 1000, 'Visa', 345678, TO_DATE('15-JAN-2023', 'DD-MON-YYYY'), TO_DATE('01-NOV-2019 08:00:00', 'DD-MON-YYYY HH24:MI:SS'), TO_DATE('01-DEC-2019 15:00:00', 'DD-MON-YYYY HH24:MI:SS'), 66666666);
Insert into Rental values(10004, 'ABC-109', 1000004, 234521, 'MasterCard', 456789, TO_DATE('15-JAN-2023', 'DD-MON-YYYY'),  TO_DATE('01-OCT-2019 20:00:00', 'DD-MON-YYYY HH24:MI:SS'), TO_DATE('10-OCT-2019 20:00:00', 'DD-MON-YYYY HH24:MI:SS'), 44444444);
Insert into Rental values(10005, 'ABC-113', 1000005, 5600, 'Visa', 567890, TO_DATE('15-JAN-2023', 'DD-MON-YYYY'), TO_DATE('01-OCT-2019 14:00:00', 'DD-MON-YYYY HH24:MI:SS'), TO_DATE('10-OCT-2019 14:00:00', 'DD-MON-YYYY HH24:MI:SS'), 55555555);

Insert into Return values(10004, TO_DATE('10-OCT-2019 20:00:00', 'DD-MON-YYYY HH24:MI:SS'), 234600, 'True', 740);
Insert into Return values(10005, TO_DATE('10-OCT-2019 14:00:00', 'DD-MON-YYYY HH24:MI:SS'), 6000, 'True', 830);

commit work;
