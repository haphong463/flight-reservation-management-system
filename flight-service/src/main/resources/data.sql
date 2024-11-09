INSERT INTO airplanes (id, name, model, manufacturer, total_seats, created_at, updated_at) VALUES
('1e9d5f80-5f30-4a2b-9a9e-1c2d3e4f5a6b', 'Boeing 777-300ER', '777-300ER', 'Boeing', 396, NOW(), NOW()),
('2a7c6d5e-4b3f-2a1b-0c9d-8e7f6a5b4c3d', 'Airbus A320', 'A320', 'Airbus', 162, NOW(), NOW()),
('3b8e7f6a-5c4d-3b2a-1e0f-9d8c7b6a5e4f', 'Boeing 787 Dreamliner', '787-8', 'Boeing', 242, NOW(), NOW());

-- Tạo cấu hình ghế cho các máy bay

-- Cấu hình ghế cho Boeing 777-300ER
INSERT INTO seat_configs (airplane_id, seat_class, seat_count) VALUES
('1e9d5f80-5f30-4a2b-9a9e-1c2d3e4f5a6b', 'ECONOMY', 300),
('1e9d5f80-5f30-4a2b-9a9e-1c2d3e4f5a6b', 'BUSINESS', 60),
('1e9d5f80-5f30-4a2b-9a9e-1c2d3e4f5a6b', 'FIRST', 36);

-- Cấu hình ghế cho Airbus A320
INSERT INTO seat_configs (airplane_id, seat_class, seat_count) VALUES
('2a7c6d5e-4b3f-2a1b-0c9d-8e7f6a5b4c3d', 'ECONOMY', 150),
('2a7c6d5e-4b3f-2a1b-0c9d-8e7f6a5b4c3d', 'BUSINESS', 12);

-- Cấu hình ghế cho Boeing 787 Dreamliner
INSERT INTO seat_configs (airplane_id, seat_class, seat_count) VALUES
('3b8e7f6a-5c4d-3b2a-1e0f-9d8c7b6a5e4f', 'ECONOMY', 200),
('3b8e7f6a-5c4d-3b2a-1e0f-9d8c7b6a5e4f', 'BUSINESS', 42);