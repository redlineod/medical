DROP TABLE IF EXISTS visits;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS doctors;

-- Пациенты
CREATE TABLE patients
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL
);

-- Врачи
CREATE TABLE doctors
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    timezone   VARCHAR(50)  NOT NULL
);

-- Визиты
CREATE TABLE visits
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_datetime DATETIME NOT NULL,
    end_datetime   DATETIME NOT NULL,
    patient_id     BIGINT   NOT NULL,
    doctor_id      BIGINT   NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES patients (id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors (id) ON DELETE CASCADE
);


-- Врачи
INSERT INTO doctors (id, first_name, last_name, timezone)
VALUES (1, 'Olena', 'Kovalenko', 'Europe/Kyiv'),
       (2, 'Ivan', 'Petrenko', 'Europe/Kyiv'),
       (3, 'Sofia', 'Melnyk', 'Europe/Warsaw'),
       (4, 'Mykola', 'Shevchenko', 'Europe/London');

-- Пациенты
INSERT INTO patients (id, first_name, last_name)
VALUES (1, 'Andriy', 'Kushnir'),
       (2, 'Iryna', 'Lysenko'),
       (3, 'Oleg', 'Bodnar'),
       (4, 'Maria', 'Tkach'),
       (5, 'Vasyl', 'Kravchuk'),
       (6, 'Natalia', 'Danylko'),
       (7, 'Petro', 'Marchenko'),
       (8, 'Halyna', 'Semenova'),
       (9, 'Andriy', 'Kravchuk');

-- Визиты (UTC)
INSERT INTO visits (id, start_datetime, end_datetime, patient_id, doctor_id)
VALUES (1, '2025-09-20 07:00:00', '2025-09-20 07:30:00', 1, 1),
       (2, '2025-09-20 08:00:00', '2025-09-20 08:30:00', 1, 2),
       (3, '2025-09-21 06:00:00', '2025-09-21 06:45:00', 2, 1),
       (4, '2025-09-21 11:00:00', '2025-09-21 11:30:00', 2, 3),
       (5, '2025-09-22 09:00:00', '2025-09-22 09:40:00', 3, 2),
       (6, '2025-09-23 15:00:00', '2025-09-23 15:30:00', 4, 4),
       (7, '2025-09-24 06:30:00', '2025-09-24 07:00:00', 5, 1),
       (8, '2025-09-24 12:00:00', '2025-09-24 12:30:00', 6, 3),
       (9, '2025-09-25 07:15:00', '2025-09-25 07:45:00', 7, 4),
       (10, '2025-09-25 08:00:00', '2025-09-25 08:30:00', 8, 2),
       (11, '2025-09-25 07:15:00', '2025-09-25 07:45:00', 9, 3),
       (12, '2025-09-25 08:00:00', '2025-09-25 08:30:00', 9, 4),
       (13, '2025-09-25 11:00:00', '2025-09-21 11:30:00', 2, 3);
